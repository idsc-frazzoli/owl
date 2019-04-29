// code by ob
package ch.ethz.idsc.sophus.filter;

import java.util.NavigableMap;
import java.util.Objects;
import java.util.TreeMap;

import ch.ethz.idsc.sophus.math.NavigableMapUnaryOperator;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Min;

// TODO OB extract "fixed radius" functionality to separate class, or extract "fixed interval" to separate class
public class NonuniformGeodesicCenterFilterNEW implements NavigableMapUnaryOperator {
  /** @param nonuniformGeodesicCenter
   * @param (temporal) interval radius
   * @return
   * @throws Exception if given geodesicCenter is null */
  public static NonuniformGeodesicCenterFilterNEW of(NonuniformGeodesicCenterNEW nonuniformGeodesicCenterNEW, Scalar interval) {
    return new NonuniformGeodesicCenterFilterNEW(Objects.requireNonNull(nonuniformGeodesicCenterNEW), interval);
  }

  // ---
  private final NonuniformGeodesicCenterNEW nonuniformGeodesicCenterNEW;
  private Scalar interval;

  private NonuniformGeodesicCenterFilterNEW(NonuniformGeodesicCenterNEW nonuniformGeodesicCenterNEW, Scalar interval) {
    this.nonuniformGeodesicCenterNEW = nonuniformGeodesicCenterNEW;
    this.interval = interval;
  }

  @Override
  public NavigableMap<Scalar, Tensor> apply(NavigableMap<Scalar, Tensor> navigableMap) {
    NavigableMap<Scalar, Tensor> resultMap = new TreeMap<>();
    if (Scalars.lessThan(interval, RealScalar.ZERO)) {
      interval = interval.negate();
      // fixed radius
      for (Scalar key : navigableMap.keySet()) {
        Scalar loKey = key;
        Scalar hiKey = key;
        for (int index = 0; index < interval.number().intValue(); ++index) {
          if (loKey.equals(navigableMap.firstKey()) || hiKey.equals(navigableMap.lastKey()))
            break;
          loKey = Min.of(loKey, navigableMap.lowerKey(loKey));
          hiKey = Min.of(hiKey, navigableMap.higherKey(hiKey));
        }
        NavigableMap<Scalar, Tensor> subMap = navigableMap.subMap(loKey, true, hiKey, true);
        // TODO OB state meaning/purpose of "RealScalar.of(-1)"
        resultMap.put(key, nonuniformGeodesicCenterNEW.apply(subMap, key, RealScalar.of(-1)));
      }
    } else {
      // // fixed interval
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
    }
    return resultMap;
  }
}
