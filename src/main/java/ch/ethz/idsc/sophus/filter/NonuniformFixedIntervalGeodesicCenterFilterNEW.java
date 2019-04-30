// code by ob
package ch.ethz.idsc.sophus.filter;

import java.util.NavigableMap;
import java.util.Objects;
import java.util.TreeMap;

import ch.ethz.idsc.sophus.math.NavigableMapUnaryOperator;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Min;

public class NonuniformFixedIntervalGeodesicCenterFilterNEW implements NavigableMapUnaryOperator {
  /** @param nonuniformGeodesicCenter
   * @param (temporal) interval radius
   * @return
   * @throws Exception if given geodesicCenter is null */
  public static NonuniformFixedIntervalGeodesicCenterFilterNEW of(NonuniformGeodesicCenterNEW nonuniformGeodesicCenterNEW, Scalar interval) {
    return new NonuniformFixedIntervalGeodesicCenterFilterNEW(Objects.requireNonNull(nonuniformGeodesicCenterNEW), interval);
  }

  // ---
  private final NonuniformGeodesicCenterNEW nonuniformGeodesicCenterNEW;
  private Scalar interval;

  private NonuniformFixedIntervalGeodesicCenterFilterNEW(NonuniformGeodesicCenterNEW nonuniformGeodesicCenterNEW, Scalar interval) {
    this.nonuniformGeodesicCenterNEW = nonuniformGeodesicCenterNEW;
    this.interval = interval;
  }

  @Override
  public NavigableMap<Scalar, Tensor> apply(NavigableMap<Scalar, Tensor> navigableMap) {
    NavigableMap<Scalar, Tensor> resultMap = new TreeMap<>();
    Scalar lo;
    Scalar hi;
    Scalar correctedInterval;
    for (Scalar key : navigableMap.keySet()) {
      lo = Min.of(key.subtract(navigableMap.firstKey()), interval);
      hi = Min.of(navigableMap.lastKey().subtract(key), interval);
      correctedInterval = Min.of(lo, hi);
      NavigableMap<Scalar, Tensor> subMap = navigableMap.subMap(key.subtract(correctedInterval), true, key.add(correctedInterval), true);
      resultMap.put(key, nonuniformGeodesicCenterNEW.apply(subMap, key, correctedInterval));
    }
    return resultMap;
  }
}
