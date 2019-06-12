// code by ob
package ch.ethz.idsc.sophus.filter.ga;

import java.io.Serializable;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.TreeMap;

import ch.ethz.idsc.sophus.math.NavigableMapUnaryOperator;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Min;

public class NonuniformFixedIntervalGeodesicCenterFilter implements NavigableMapUnaryOperator, Serializable {
  /** @param nonuniformGeodesicCenter
   * @param (temporal) interval radius
   * @return
   * @throws Exception if given geodesicCenter is null */
  public static NonuniformFixedIntervalGeodesicCenterFilter of(NonuniformFixedIntervalGeodesicCenter nonuniformFixedIntervalGeodesicCenter, Scalar interval) {
    return new NonuniformFixedIntervalGeodesicCenterFilter(Objects.requireNonNull(nonuniformFixedIntervalGeodesicCenter), interval);
  }

  // ---
  private final NonuniformFixedIntervalGeodesicCenter nonuniformFixedIntervalGeodesicCenter;
  private Scalar interval;

  private NonuniformFixedIntervalGeodesicCenterFilter(NonuniformFixedIntervalGeodesicCenter nonuniformFixedIntervalGeodesicCenter, Scalar interval) {
    this.nonuniformFixedIntervalGeodesicCenter = nonuniformFixedIntervalGeodesicCenter;
    this.interval = interval;
  }

  @Override // from NavigableMapUnaryOperator
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
      resultMap.put(key, nonuniformFixedIntervalGeodesicCenter.apply(subMap, key, correctedInterval));
    }
    return resultMap;
  }
}
