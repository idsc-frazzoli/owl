// code by jph
package ch.ethz.idsc.sophus.app.api;

import ch.ethz.idsc.sophus.math.TensorMetric;
import ch.ethz.idsc.sophus.math.WeightingInterface;

@FunctionalInterface
public interface MetricWeighting {
  /** @param flattenLogManifold
   * @param tensorMetric
   * @return */
  WeightingInterface from(TensorMetric tensorMetric);
}
