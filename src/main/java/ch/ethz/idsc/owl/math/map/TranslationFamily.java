// code by jph
package ch.ethz.idsc.owl.math.map;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/** the term "family" conveys the meaning that the translation
 * depends on a single parameter, for instance time */
public abstract class TranslationFamily implements RigidFamily {
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
    Tensor matrix = IdentityMatrix.of(3);
    Tensor offset = function_apply(scalar);
    matrix.set(offset.Get(0), 0, 2);
    matrix.set(offset.Get(1), 1, 2);
    return matrix;
  }

  /** @param scalar
   * @return translation at given scalar parameter */
  public abstract Tensor function_apply(Scalar scalar);
}
