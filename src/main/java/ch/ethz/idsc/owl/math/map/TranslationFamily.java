// code by jph
package ch.ethz.idsc.owl.math.map;

import java.io.Serializable;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/** the term "family" conveys the meaning that the translation
 * depends on a single parameter, for instance time */
public abstract class TranslationFamily implements RigidFamily, Serializable {
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
    return Se2Utils.toSE2Translation(function_apply(scalar));
  }

  /** @param scalar
   * @return translation at given scalar parameter */
  public abstract Tensor function_apply(Scalar scalar);
}
