// code by jph
package ch.ethz.idsc.sophus.app.bd1;

import ch.ethz.idsc.sophus.hs.FlattenLogManifold;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorScalarFunction;

@FunctionalInterface
interface HsScalarFunction {
  /** @param flattenLogManifold
   * @param sequence
   * @param values
   * @return */
  TensorScalarFunction build(FlattenLogManifold flattenLogManifold, Tensor sequence, Tensor values);
}
