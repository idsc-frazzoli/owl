// code by jph
package ch.ethz.idsc.sophus.app.api;

import ch.ethz.idsc.sophus.hs.FlattenLogManifold;
import ch.ethz.idsc.sophus.math.TensorMetric;
import ch.ethz.idsc.sophus.math.WeightingInterface;

@FunctionalInterface
public interface LogMetricWeighting {
  /** @param flattenLogManifold
   * @param tensorMetric
   * @return */
  WeightingInterface from(FlattenLogManifold flattenLogManifold, TensorMetric tensorMetric);
}
