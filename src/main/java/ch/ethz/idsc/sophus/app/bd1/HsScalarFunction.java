// code by jph
package ch.ethz.idsc.sophus.app.bd1;

import ch.ethz.idsc.sophus.hs.FlattenLogManifold;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorScalarFunction;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

@FunctionalInterface
interface HsScalarFunction {
  /** @param flattenLogManifold
   * @param variogram
   * @param sequence
   * @param values
   * @return */
  TensorScalarFunction build( //
      FlattenLogManifold flattenLogManifold, ScalarUnaryOperator variogram, Tensor sequence, Tensor values);
}
