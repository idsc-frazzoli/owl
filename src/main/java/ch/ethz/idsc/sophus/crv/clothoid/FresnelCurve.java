// code by jph
package ch.ethz.idsc.sophus.crv.clothoid;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;

public enum FresnelCurve implements ScalarTensorFunction {
  FUNCTION;
  // ---
  @Override
  public Tensor apply(Scalar scalar) {
    return Tensors.of( //
        Fresnel.C().apply(scalar), //
        Fresnel.S().apply(scalar));
  }
}
