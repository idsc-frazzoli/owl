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

// TODO OB extract "fixed radius" functionality to separate class, or extract "fixed interval" to separate class
public class NonuniformFixedRadiusGeodesicCenterFilterNEW implements NavigableMapUnaryOperator {
  /** @param nonuniformGeodesicCenter
   * @param (temporal) interval radius
   * @return
   * @throws Exception if given geodesicCenter is null */
  public static NonuniformFixedRadiusGeodesicCenterFilterNEW of(NonuniformGeodesicCenterNEW nonuniformGeodesicCenterNEW, Scalar radius) {
    return new NonuniformFixedRadiusGeodesicCenterFilterNEW(Objects.requireNonNull(nonuniformGeodesicCenterNEW), radius);
  }

  // ---
  private final NonuniformGeodesicCenterNEW nonuniformGeodesicCenterNEW;
  private Scalar radius;

  private NonuniformFixedRadiusGeodesicCenterFilterNEW(NonuniformGeodesicCenterNEW nonuniformGeodesicCenterNEW, Scalar radius) {
    this.nonuniformGeodesicCenterNEW = nonuniformGeodesicCenterNEW;
    this.radius = radius;
  }

  // private NavigableMap<Scalar, Tensor> fixedRadius(NavigableMap<Scalar, Tensor> navigableMap) {
  // NavigableMap<Scalar, Tensor> resultMap = new TreeMap<>();
  // for (Scalar key : navigableMap.keySet()) {
  // Scalar loKey = key;
  // Scalar hiKey = key;
  // for (int index = 0; index < radius.number().intValue(); ++index) {
  // if (loKey.equals(navigableMap.firstKey()) || hiKey.equals(navigableMap.lastKey()))
  // break;
  // loKey = Max.of(navigableMap.lowerKey(loKey), navigableMap.firstKey());
  // hiKey = Min.of(navigableMap.higherKey(hiKey), navigableMap.lastKey());
  // // Why does this look so much nicer?
  // // loKey = Min.of(loKey, navigableMap.lowerKey(loKey));
  // // hiKey = Min.of(hiKey, navigableMap.higherKey(hiKey));
  // }
  // NavigableMap<Scalar, Tensor> subMap = navigableMap.subMap(loKey, true, hiKey, true);
  // resultMap.put(key, nonuniformGeodesicCenterNEW.apply(subMap, key, radius));
  // }
  // return resultMap;
  // }
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
        // Why does this look so much nicer?
        // loKey = Min.of(loKey, navigableMap.lowerKey(loKey));
        // hiKey = Min.of(hiKey, navigableMap.higherKey(hiKey));
      }
      NavigableMap<Scalar, Tensor> subMap = navigableMap.subMap(loKey, true, hiKey, true);
      resultMap.put(key, nonuniformGeodesicCenterNEW.apply(subMap, key, radius));
    }
    return resultMap;
  }
}
