// code by jph
package ch.ethz.idsc.sophus.hs.r2;

import java.io.Serializable;

import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.lie.RotationMatrix;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** the term "family" conveys the meaning that the rigid transformation
 * depends on a single parameter, for instance time */
public final class Se2Family implements R2RigidFamily, Serializable {
  /** @param center
   * @param rotation
   * @return */
  public static R2RigidFamily rotationAround(Tensor center, ScalarUnaryOperator rotation) {
    return new Se2Family(time -> {
      Scalar theta = rotation.apply(time);
      return center.subtract(RotationMatrix.of(theta).dot(center)).append(theta);
    });
  }

  // ---
  private final ScalarTensorFunction function;

  /** @param function maps a {@link Scalar} to a vector {px, py, angle}
   * that represents the {@link Se2Bijection} */
  public Se2Family(ScalarTensorFunction function) {
    this.function = function;
  }

  @Override // from BijectionFamily
  public TensorUnaryOperator forward(Scalar scalar) {
    return new Se2ForwardAction(function.apply(scalar));
  }

  @Override // from BijectionFamily
  public TensorUnaryOperator inverse(Scalar scalar) {
    return new Se2InverseAction(function.apply(scalar));
  }

  @Override // from RigidFamily
  public Tensor forward_se2(Scalar scalar) {
    return Se2Matrix.of(function.apply(scalar));
  }
}
