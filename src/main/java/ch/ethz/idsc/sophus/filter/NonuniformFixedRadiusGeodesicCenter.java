// code by ob
package ch.ethz.idsc.sophus.filter;

import java.util.NavigableMap;
import java.util.Objects;

import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Reverse;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.sca.Power;
import ch.ethz.idsc.tensor.sca.Sign;

public class NonuniformFixedRadiusGeodesicCenter {
  /** @param geodesicInterface
   * @param function that maps the (temporally) neighborhood of a control point to a weight mask
   * @return operator that maps a sequence of points to their geodesic center
   * @throws Exception if either input parameter is null */
  public static NonuniformFixedRadiusGeodesicCenter of(GeodesicInterface geodesicInterface) {
    return new NonuniformFixedRadiusGeodesicCenter(Objects.requireNonNull(geodesicInterface));
  }

  // ---
  public final GeodesicInterface geodesicInterface;

  /* package */ NonuniformFixedRadiusGeodesicCenter(GeodesicInterface geodesicInterface) {
    this.geodesicInterface = geodesicInterface;
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
   * @param filterradius
   * @return */
  private static Tensor splits(NavigableMap<Scalar, Tensor> subMap, Scalar key, Scalar radius) {
    Scalar exponent = RealScalar.of(2);
    Tensor maskLeft = Tensors.empty();
    Tensor maskRight = Tensors.empty();
    // TODO OB: This does not look right.. aka. not canonical?
    for (Scalar headMapKey : subMap.headMap(key, false).keySet()) {
      maskLeft.append(Power.of(RealScalar.ONE.add(key.subtract(headMapKey)).reciprocal(), exponent));
    }
    for (Scalar tailMapKey : subMap.tailMap(key, false).descendingKeySet()) {
      maskRight.append(Power.of(RealScalar.ONE.add(tailMapKey.subtract(key)).reciprocal(), exponent));
    }
    maskLeft.append(RationalScalar.HALF);
    maskRight.append(RationalScalar.HALF);
    Tensor splitsLeft = maskToSplits(maskLeft);
    Tensor splitsRight = Reverse.of(maskToSplits(maskRight));
    Tensor splitsFinal = maskToSplits(Tensors.of(Total.of(maskLeft), Total.of(maskRight)));
    return Tensors.of(splitsLeft, splitsRight, splitsFinal);
  }

  public Tensor apply(NavigableMap<Scalar, Tensor> subMap, Scalar key, Scalar radius) {
    // TODO OB: require Tensor.length = geodesicInterface."length", also 3 fuer se2?
    key = Sign.requirePositiveOrZero(key);
    radius = Sign.requirePositiveOrZero(radius);
    Tensor tempL = subMap.firstEntry().getValue();
    Tensor tempR = subMap.lastEntry().getValue();
    Tensor splits = splits(subMap, key, radius);
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
    Tensor result = geodesicInterface.split(tempL, tempR, splits.get(2).Get(0));
    return result;
  }
}