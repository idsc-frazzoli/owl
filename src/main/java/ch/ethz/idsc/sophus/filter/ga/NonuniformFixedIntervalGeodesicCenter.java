// code by ob
package ch.ethz.idsc.sophus.filter.ga;

import java.io.Serializable;
import java.util.NavigableMap;
import java.util.Objects;

import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.sca.Sign;

// TODO OB reduce redundancy with NonuniformFixedRadiusGeodesicCenter
public class NonuniformFixedIntervalGeodesicCenter implements Serializable {
  /** @param geodesicInterface
   * @param function that maps the (temporally) neighborhood of a control point to a weight mask
   * @return operator that maps a sequence of points to their geodesic center
   * @throws Exception if either input parameter is null */
  public static NonuniformFixedIntervalGeodesicCenter of(GeodesicInterface geodesicInterface, ScalarUnaryOperator smoothingKernel) {
    return new NonuniformFixedIntervalGeodesicCenter(Objects.requireNonNull(geodesicInterface), Objects.requireNonNull(smoothingKernel));
  }

  // ---
  public final GeodesicInterface geodesicInterface;
  private final ScalarUnaryOperator windowFunction;

  /* package */ NonuniformFixedIntervalGeodesicCenter(GeodesicInterface geodesicInterface, ScalarUnaryOperator windowFunction) {
    this.geodesicInterface = geodesicInterface;
    this.windowFunction = windowFunction;
  }

  private static Tensor maskToSplits(Tensor mask) {
    Tensor result = Tensors.empty();
    Scalar factor = mask.Get(0);
    for (int index = 1; index < mask.length(); ++index) {
      factor = factor.add(mask.Get(index));
      result.append(mask.Get(index).divide(factor));
    }
    return result;
  }

  /** @param subMap
   * @param key: timestamp to be evaluated
   * @param interval of considered neighborhood [key - interval, key + interval]
   * @return */
  private Tensor splits(NavigableMap<Scalar, Tensor> subMap, Scalar key, Scalar interval) {
    Scalar doubleInterval = interval.add(interval);
    Tensor maskLeft = Tensors.empty();
    Tensor maskRight = Tensors.empty();
    for (Scalar headMapKey : subMap.headMap(key, false).keySet()) {
      maskLeft.append(windowFunction.apply(headMapKey.subtract(key).divide(doubleInterval)));
    }
    for (Scalar tailMapKey : subMap.tailMap(key, false).descendingKeySet()) {
      maskRight.append(windowFunction.apply(tailMapKey.subtract(key).divide(doubleInterval)));
    }
    maskLeft.append(RationalScalar.HALF);
    maskRight.append(RationalScalar.HALF);
    Tensor splitsLeft = maskToSplits(maskLeft);
    Tensor splitsRight = maskToSplits(maskRight);
    Tensor splitsFinal = maskToSplits(Tensors.of(Total.of(maskLeft), Total.of(maskRight)));
    return Tensors.of(splitsLeft, splitsRight, splitsFinal);
  }

  public synchronized Tensor apply(NavigableMap<Scalar, Tensor> subMap, Scalar key, Scalar interval) {
    key = Sign.requirePositiveOrZero(key);
    interval = Sign.requirePositiveOrZero(interval);
    Tensor tempL = subMap.firstEntry().getValue();
    Tensor tempR = subMap.lastEntry().getValue();
    Tensor splits = splits(subMap, key, interval);
    // System.out.println(splits);
    int index = 0;
    // subMap on the left side: (first_key, key] both excluded
    for (Scalar headMapKey : subMap.subMap(subMap.firstKey(), false, key, true).keySet()) {
      tempL = geodesicInterface.split(tempL, subMap.get(headMapKey), splits.get(0).Get(index));
      index += 1;
    }
    index = 0;
    // subMap on the right side: [key, last_key)
    for (Scalar tailMapKey : subMap.subMap(key, true, subMap.lastKey(), false).descendingKeySet()) {
      tempR = geodesicInterface.split(tempR, subMap.get(tailMapKey), splits.get(1).Get(index));
      index += 1;
    }
    Tensor result = geodesicInterface.split(tempL, tempR, splits.get(2).Get(0));
    return result;
  }
}