// code by jph
package ch.ethz.idsc.sophus.hs.r2;

import java.io.Serializable;

import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/** the term "family" conveys the meaning that the translation
 * depends on a single parameter, for instance time */
public abstract class R2TranslationFamily implements R2RigidFamily, Serializable {
  @Override // from BijectionFamily
  public final TensorUnaryOperator forward(Scalar scalar) {
    Tensor offset = function_apply(scalar);
    return tensor -> tensor.add(offset);
  }

  @Override // from BijectionFamily
  public final TensorUnaryOperator inverse(Scalar scalar) {
    Tensor offset = function_apply(scalar);
    return tensor -> tensor.subtract(offset);
  }

  @Override // from RigidFamily
  public final Tensor forward_se2(Scalar scalar) {
    return Se2Matrix.translation(function_apply(scalar)); // TODO JPH test coverage
  }

  /** @param scalar
   * @return translation at given scalar parameter */
  public abstract Tensor function_apply(Scalar scalar);
}
