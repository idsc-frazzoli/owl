// code by ob
package ch.ethz.idsc.sophus.filter;

import java.util.NavigableMap;
import java.util.Objects;
import java.util.TreeMap;

import ch.ethz.idsc.sophus.math.NavigableMapUnaryOperator;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.red.Min;
import ch.ethz.idsc.tensor.sca.Sign;

public class NonuniformFixedRadiusGeodesicCenterFilter implements NavigableMapUnaryOperator {
  /** @param nonuniformFixedRadiusGeodesicCenter
   * @param radius
   * @return
   * @throws Exception given if nonuniformFixedRadiusGeodesicCenter is null */
  public static NonuniformFixedRadiusGeodesicCenterFilter of( //
      NonuniformFixedRadiusGeodesicCenter nonuniformFixedRadiusGeodesicCenter, Scalar radius) {
    return new NonuniformFixedRadiusGeodesicCenterFilter( //
        Objects.requireNonNull(nonuniformFixedRadiusGeodesicCenter), radius);
  }

  // ---
  private final NonuniformFixedRadiusGeodesicCenter nonuniformFixedRadiusGeodesicCenter;
  private final Scalar radius;

  private NonuniformFixedRadiusGeodesicCenterFilter( //
      NonuniformFixedRadiusGeodesicCenter nonuniformFixedRadiusGeodesicCenter, Scalar radius) {
    this.nonuniformFixedRadiusGeodesicCenter = nonuniformFixedRadiusGeodesicCenter;
    this.radius = Sign.requirePositive(radius);
    // TODO OB if radius is required to be an integer, then use int, or IntegerQ.require(radius);
  }

  @Override
  public NavigableMap<Scalar, Tensor> apply(NavigableMap<Scalar, Tensor> navigableMap) {
    NavigableMap<Scalar, Tensor> resultMap = new TreeMap<>();
    for (Scalar key : navigableMap.keySet()) {
      Scalar loKey = key;
      Scalar hiKey = key;
      for (int index = 0; index < radius.number().intValue(); ++index) {
        if (loKey.equals(navigableMap.firstKey()) || hiKey.equals(navigableMap.lastKey()))
          break;
        loKey = Max.of(navigableMap.lowerKey(loKey), navigableMap.firstKey());
        hiKey = Min.of(navigableMap.higherKey(hiKey), navigableMap.lastKey());
      }
      NavigableMap<Scalar, Tensor> subMap = navigableMap.subMap(loKey, true, hiKey, true);
      resultMap.put(key, nonuniformFixedRadiusGeodesicCenter.apply(subMap, key, radius));
    }
    return resultMap;
  }
}
