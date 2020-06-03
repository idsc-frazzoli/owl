// code by jph
package ch.ethz.idsc.sophus.app.api;

import ch.ethz.idsc.sophus.hs.VectorLogManifold;
import ch.ethz.idsc.sophus.math.WeightingInterface;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

@FunctionalInterface
public interface LogWeighting {
  /** @param flattenLogManifold
   * @param variogram
   * @return */
  WeightingInterface from(VectorLogManifold flattenLogManifold, ScalarUnaryOperator variogram);
}
