// code by jph
package ch.ethz.idsc.sophus.app.bd1;

import ch.ethz.idsc.sophus.hs.VectorLogManifold;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorScalarFunction;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

@FunctionalInterface
interface HsScalarFunction {
  /** @param vectorLogManifold
   * @param variogram
   * @param sequence
   * @param values
   * @return */
  TensorScalarFunction build( //
      VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence, Tensor values);
}
