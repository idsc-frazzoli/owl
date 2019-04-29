// code by ob
package ch.ethz.idsc.sophus.filter;

import java.util.NavigableMap;
import java.util.Objects;
import java.util.TreeMap;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Min;

public class NonuniformGeodesicCenterFilterNEW {
  /** @param nonuniformGeodesicCenter
   * @param (temporal) radius
   * @return
   * @throws Exception if given geodesicCenter is null */
  public static NonuniformGeodesicCenterFilterNEW of(NonuniformGeodesicCenterNEW nonuniformGeodesicCenterNEW, Scalar radius) {
    return new NonuniformGeodesicCenterFilterNEW(Objects.requireNonNull(nonuniformGeodesicCenterNEW), radius);
  }

  // ---
  private final Scalar radius;
  private NonuniformGeodesicCenterNEW nonuniformGeodesicCenterNEW;

  private NonuniformGeodesicCenterFilterNEW(NonuniformGeodesicCenterNEW nonuniformGeodesicCenterNEW, Scalar radius) {
    this.nonuniformGeodesicCenterNEW = nonuniformGeodesicCenterNEW;
    this.radius = radius;
  }

  public NavigableMap<Scalar, Tensor> apply(NavigableMap<Scalar, Tensor> navigableMap) {
    NavigableMap<Scalar, Tensor> resultMap = new TreeMap<>();
    Scalar lo;
    Scalar hi;
    Scalar interval;
    for (Scalar key : navigableMap.keySet()) {
      lo = Min.of(key.subtract(navigableMap.firstKey()), radius);
      hi = Min.of(navigableMap.lastKey().subtract(key), radius);
      interval = Min.of(lo, hi);
      NavigableMap<Scalar, Tensor> subMap = navigableMap.subMap(key.subtract(interval), true, key.add(interval), true);
      resultMap.put(key, nonuniformGeodesicCenterNEW.apply(subMap, key, interval));
    }
    return resultMap;
  }
}
