// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.util.Objects;

import ch.ethz.idsc.sophus.hs.VectorLogManifold;
import ch.ethz.idsc.sophus.math.WeightingInterface;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

@FunctionalInterface
public interface LogWeighting {
  /** @param weightingInterface
   * @param sequence
   * @return */
  static TensorUnaryOperator wrap(WeightingInterface weightingInterface, Tensor sequence) {
    Objects.requireNonNull(weightingInterface);
    Objects.requireNonNull(sequence);
    return point -> weightingInterface.weights(sequence, point);
  }

  /** @param vectorLogManifold
   * @param variogram
   * @param sequence
   * @return */
  TensorUnaryOperator from(VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence);
}
