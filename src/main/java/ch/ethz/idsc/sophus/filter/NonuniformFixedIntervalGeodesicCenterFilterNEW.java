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
  public static NonuniformFixedIntervalGeodesicCenterFilterNEW of(NonuniformFixedIntervalGeodesicCenterNEW nonuniformFixedIntervalGeodesicCenterNEW,
      Scalar interval) {
    return new NonuniformFixedIntervalGeodesicCenterFilterNEW(Objects.requireNonNull(nonuniformFixedIntervalGeodesicCenterNEW), interval);
  }

  // ---
  private final NonuniformFixedIntervalGeodesicCenterNEW nonuniformFixedIntervalGeodesicCenterNEW;
  private Scalar interval;

  private NonuniformFixedIntervalGeodesicCenterFilterNEW(NonuniformFixedIntervalGeodesicCenterNEW nonuniformFixedIntervalGeodesicCenterNEW, Scalar interval) {
    this.nonuniformFixedIntervalGeodesicCenterNEW = nonuniformFixedIntervalGeodesicCenterNEW;
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
      resultMap.put(key, nonuniformFixedIntervalGeodesicCenterNEW.apply(subMap, key, correctedInterval));
    }
    return resultMap;
  }
}
