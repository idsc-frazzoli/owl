// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.util.Objects;

import ch.ethz.idsc.sophus.hs.VectorLogManifold;
import ch.ethz.idsc.sophus.krg.PseudoDistances;
import ch.ethz.idsc.sophus.math.WeightingInterface;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorScalarFunction;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

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
  TensorUnaryOperator from(PseudoDistances pseudoDistances, VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence);

  /** @param pseudoDistances
   * @param vectorLogManifold
   * @param variogram
   * @param sequence
   * @param values
   * @return */
  TensorScalarFunction build( //
      PseudoDistances pseudoDistances, VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence, Tensor values);
}
