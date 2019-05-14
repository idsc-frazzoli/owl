// code by ob
package ch.ethz.idsc.sophus.filter;

import java.util.NavigableMap;
import java.util.Objects;

import ch.ethz.idsc.sophus.math.GeodesicInterface;
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
  private final GeodesicInterface geodesicInterface;

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

  public static Tensor weights(NavigableMap<Scalar, Tensor> subMap, Scalar key) {
    Scalar startingWeight = RealScalar.of(1);
    // ---
    Tensor maskLeft = Tensors.vector(startingWeight.number().floatValue() / 2);
    Tensor maskRight = Tensors.vector(startingWeight.number().floatValue() / 2);
    // ---
    Scalar delta;
    Scalar prevKey = key;
    Scalar nextKey = key;
    // ---
    for (int index = 0; index < (subMap.size() - 1) / 2; ++index) {
      prevKey = subMap.lowerKey(prevKey);
      nextKey = subMap.higherKey(nextKey);
      delta = nextKey.subtract(prevKey);
      // TODO OB: make a good choice for exponent
      maskLeft.append(Power.of(key.subtract(prevKey).divide(delta), RealScalar.ONE));
      maskRight.append(Power.of(nextKey.subtract(key).divide(delta), RealScalar.ONE));
    }
    return Tensors.of(maskLeft, maskRight);
  }

  /** @param subMap
   * @param key: timestamp to be evaluated
   * @return */
  private static Tensor splits(NavigableMap<Scalar, Tensor> subMap, Scalar key) {
    Tensor maskLeft = Tensors.empty();
    Tensor maskRight = Tensors.empty();
    maskLeft = weights(subMap, key).get(0);
    maskRight = weights(subMap, key).get(1);
    Tensor splitsLeft = maskToSplits(maskLeft);
    Tensor splitsRight = maskToSplits(maskRight);
    Tensor splitsFinal = maskToSplits(Tensors.of(Total.of(maskLeft), Total.of(maskRight)));
    return Tensors.of(splitsLeft, splitsRight, splitsFinal);
  }

  public Tensor apply(NavigableMap<Scalar, Tensor> subMap, Scalar key) {
    Scalars.isZero(Abs.FUNCTION.apply(RealScalar.of(subMap.headMap(key, false).size() - subMap.tailMap(key, false).size())));
    // ---
    key = Sign.requirePositiveOrZero(key);
    Tensor tempL = subMap.firstEntry().getValue();
    Tensor tempR = subMap.lastEntry().getValue();
    Tensor splits = splits(subMap, key);
    int index = 0;
    // subMap on the left side: (first_key, key]
    for (Scalar headMapKey : subMap.subMap(subMap.firstKey(), false, key, true).descendingKeySet()) {
      tempL = geodesicInterface.split(tempL, subMap.get(headMapKey), splits.get(0).Get(index));
      ++index;
    }
    index = 0;
    // subMap on the right side: [key, last_key)
    for (Scalar tailMapKey : subMap.subMap(key, true, subMap.lastKey(), false).keySet()) {
      tempR = geodesicInterface.split(tempR, subMap.get(tailMapKey), splits.get(1).Get(index));
      ++index;
    }
    return geodesicInterface.split(tempL, tempR, splits.get(2).Get(0));
  }
}