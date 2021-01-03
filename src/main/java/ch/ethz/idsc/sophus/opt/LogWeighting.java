// code by jph
package ch.ethz.idsc.sophus.opt;

import ch.ethz.idsc.sophus.hs.Biinvariant;
import ch.ethz.idsc.sophus.hs.VectorLogManifold;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.api.TensorScalarFunction;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;

public interface LogWeighting {
  /** @param biinvariant
   * @param vectorLogManifold
   * @param variogram
   * @param sequence
   * @return */
  TensorUnaryOperator operator( //
      Biinvariant biinvariant, VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, Tensor sequence);

  /** @param biinvariant
   * @param vectorLogManifold
   * @param variogram
   * @param sequence
   * @param values
   * @return */
  TensorScalarFunction function( //
      Biinvariant biinvariant, VectorLogManifold vectorLogManifold, ScalarUnaryOperator variogram, //
      Tensor sequence, Tensor values);
}
