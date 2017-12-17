// code by jph
package ch.ethz.idsc.owl.math.map;

import ch.ethz.idsc.owl.math.ScalarTensorFunction;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.lie.RotationMatrix;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** the term "family" conveys the meaning that the rigid transformation
 * depends on a single parameter, for instance time */
public class Se2Family implements RigidFamily {
  /** @param center
   * @param rotation
   * @return */
  public static RigidFamily rotationAround(Tensor center, ScalarUnaryOperator rotation) {
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
    return new Se2Bijection(function.apply(scalar)).inverse();
  }

  @Override // from RigidFamily
  public Tensor forward_se2(Scalar scalar) {
    return new Se2Bijection(function.apply(scalar)).forward_se2();
  }
}
