// code by ob
package ch.ethz.idsc.sophus.filter;

import java.util.NavigableMap;
import java.util.Objects;

import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.sca.Abs;
import ch.ethz.idsc.tensor.sca.Power;
import ch.ethz.idsc.tensor.sca.Sign;

public class NonuniformFixedRadiusGeodesicCenter {
  /** @param geodesicInterface
   * @return operator that maps a chronological, symmetric sequence of points to their geodesic center
   * @throws Exception if geodesicInterface is null */
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

  // // This is the crucial part I need to work on sunday
  // private static Tensor weights(NavigableMap<Scalar, Tensor> navigableMap, Scalar key) {
  // Tensor result = Tensors.empty();
  // Scalar keyLeft = key;
  // Scalar keyRight = key;
  // Tensor leftWeights = Tensors.empty();
  // Tensor rightWeights = Tensors.empty();
  // Scalar remaining = RealScalar.of(1 - Math.E);
  // for (int index = 1; index < (navigableMap.size() - 1) / 2; ++index) {
  // keyLeft = navigableMap.lowerKey(keyLeft);
  // keyRight = navigableMap.higherKey(keyRight);
  // leftWeights.append(RealScalar.of(Math.exp(-index + 1)));
  // Math.exp(-index);
  //
  // }
  // return Tensors.empty();
  // }
  /** @param subMap
   * @param key: timestamp to be evaluated
   * @return */
  private static Tensor splits(NavigableMap<Scalar, Tensor> subMap, Scalar key) {
    Scalar exponent = RealScalar.of(2);
    Tensor maskLeft = Tensors.empty();
    Tensor maskRight = Tensors.empty();
    // TODO OB: This does not look right.. aka. not canonical?
    for (Scalar headMapKey : subMap.headMap(key, false).keySet())
      maskLeft.append(Power.of(RealScalar.ONE.add(key.subtract(headMapKey)).reciprocal(), exponent));
    for (Scalar tailMapKey : subMap.tailMap(key, false).descendingKeySet())
      maskRight.append(Power.of(RealScalar.ONE.add(tailMapKey.subtract(key)).reciprocal(), exponent));
    maskLeft.append(RationalScalar.HALF);
    maskRight.append(RationalScalar.HALF);
    Tensor splitsLeft = maskToSplits(maskLeft);
    Tensor splitsRight = maskToSplits(maskRight);
    Tensor splitsFinal = maskToSplits(Tensors.of(Total.of(maskLeft), Total.of(maskRight)));
    return Tensors.of(splitsLeft, splitsRight, splitsFinal);
  }

  public Tensor apply(NavigableMap<Scalar, Tensor> subMap, Scalar key) {
    // TODO OB: require Tensor.length = geodesicInterface."length", also 3 fuer se2?
    // Check that submap is of symmetric length around the given key: TODO OB/JPH how can I solve this more beautiful?
    Scalars.isZero(Abs.FUNCTION.apply(RealScalar.of(subMap.headMap(key, false).size() - subMap.tailMap(key, false).size())));
    // ---
    key = Sign.requirePositiveOrZero(key);
    Tensor tempL = subMap.firstEntry().getValue();
    Tensor tempR = subMap.lastEntry().getValue();
    Tensor splits = splits(subMap, key);
    int index = 0;
    // subMap on the left side: (first_key, key]
    for (Scalar headMapKey : subMap.subMap(subMap.firstKey(), false, key, true).keySet()) {
      tempL = geodesicInterface.split(tempL, subMap.get(headMapKey), splits.get(0).Get(index));
      ++index;
    }
    index = 0;
    // subMap on the right side: [key, last_key)
    for (Scalar tailMapKey : subMap.subMap(key, true, subMap.lastKey(), false).descendingKeySet()) {
      tempR = geodesicInterface.split(tempR, subMap.get(tailMapKey), splits.get(1).Get(index));
      ++index;
    }
    return geodesicInterface.split(tempL, tempR, splits.get(2).Get(0));
  }
}