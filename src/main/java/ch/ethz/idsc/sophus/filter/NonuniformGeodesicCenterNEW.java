// code by ob
package ch.ethz.idsc.sophus.filter;

import java.util.NavigableMap;
import java.util.Objects;

import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.sophus.math.SmoothingKernel;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Normalize;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Total;

public class NonuniformGeodesicCenterNEW {
  private static final TensorUnaryOperator NORMALIZE = Normalize.with(Total::ofVector);

  /** @param geodesicInterface
   * @param function that maps the (temporally) neighborhood of a control point to a weight mask
   * @return operator that maps a sequence of points to their geodesic center
   * @throws Exception if either input parameter is null */
  public static NonuniformGeodesicCenterNEW of(GeodesicInterface geodesicInterface, SmoothingKernel smoothingKernel) {
    return new NonuniformGeodesicCenterNEW(Objects.requireNonNull(geodesicInterface), smoothingKernel);
  }

  // ---
  public final GeodesicInterface geodesicInterface;
  private final SmoothingKernel smoothingKernel;

  /* package */ NonuniformGeodesicCenterNEW(GeodesicInterface geodesicInterface, SmoothingKernel smoothingKernel) {
    this.geodesicInterface = geodesicInterface;
    this.smoothingKernel = smoothingKernel;
  }

  private Tensor splits(NavigableMap<Scalar, Tensor> subMap, Scalar key, Scalar interval) {
    Tensor maskLeft = Tensors.empty();
    Tensor maskRight = Tensors.empty();
    for (Scalar subMapKey : subMap.keySet()) {
      if (Scalars.lessThan(subMapKey, key))
        maskLeft.append(smoothingKernel.apply(subMapKey.subtract(key).divide(interval.add(interval))));
      else if (subMapKey.equals(key)) {
        maskLeft.append(RationalScalar.HALF);
        maskRight.append(RationalScalar.HALF);
      } else
        maskRight.append(smoothingKernel.apply(subMapKey.subtract(key).divide(interval.add(interval))));
    }
    Tensor splitsLeft = StaticHelperCausal.splits(NORMALIZE.apply(maskLeft));
    Tensor splitsRight = StaticHelperCausal.splits(NORMALIZE.apply(maskRight));
    Scalar splitsFinal = StaticHelperCausal.splits(NORMALIZE.apply(Tensors.of(Total.of(maskRight), Total.of(maskLeft)))).Get(0);
    return Tensors.of(splitsLeft, splitsRight, splitsFinal);
  }

  public synchronized Tensor apply(NavigableMap<Scalar, Tensor> subMap, Scalar key, Scalar interval) {
    Tensor tempL = subMap.firstEntry().getValue();
    Tensor tempR = subMap.lastEntry().getValue();
    Tensor splits = splits(subMap, key, interval);
    int index = 0;
    for (Scalar headMapKey : subMap.headMap(key, false).keySet()) {
      tempL = geodesicInterface.split(tempL, subMap.get(subMap.higherKey(headMapKey)), splits.get(0).Get(index));
      index += 1;
    }
    index = 0;
    for (Scalar tailMapKey : subMap.tailMap(key, false).descendingKeySet()) {
      tempR = geodesicInterface.split(subMap.get(tailMapKey), tempR, splits.get(1).Get(index));
      index += 1;
    }
    Tensor result = geodesicInterface.split(tempL, tempR, splits.Get(2));
    return result;
  }
}