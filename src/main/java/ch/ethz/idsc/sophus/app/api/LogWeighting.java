// code by jph
package ch.ethz.idsc.sophus.app.api;

import ch.ethz.idsc.sophus.hs.VectorLogManifold;
import ch.ethz.idsc.sophus.math.WeightingInterface;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

//@FunctionalInterface
public interface LogWeighting {
  /** @param flattenLogManifold
   * @param variogram
   * @return */
  WeightingInterface from(VectorLogManifold flattenLogManifold, ScalarUnaryOperator variogram);

  /** @param flattenLogManifold
   * @param variogram
   * @param sequence
   * @return */
  default TensorUnaryOperator ops(VectorLogManifold flattenLogManifold, ScalarUnaryOperator variogram, Tensor sequence) {
    WeightingInterface weightingInterface = from(flattenLogManifold, variogram);
    return point -> weightingInterface.weights(sequence, point);
  }
}
