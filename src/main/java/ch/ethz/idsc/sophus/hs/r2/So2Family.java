// code by jph
package ch.ethz.idsc.sophus.hs.r2;

import java.io.Serializable;

import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.lie.RotationMatrix;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** the term "family" conveys the meaning that the rotation
 * depends on a single parameter, for instance time */
public class So2Family implements R2RigidFamily, Serializable {
  private final ScalarUnaryOperator function;

  /** @param function that maps to angle */
  public So2Family(ScalarUnaryOperator function) {
    this.function = function;
  }

  @Override // from BijectionFamily
  public TensorUnaryOperator forward(Scalar scalar) {
    Scalar angle = function.apply(scalar);
    Tensor matrix = RotationMatrix.of(angle);
    return tensor -> matrix.dot(tensor);
  }

  @Override // from BijectionFamily
  public TensorUnaryOperator inverse(Scalar scalar) {
    Scalar angle = function.apply(scalar);
    Tensor matrix = RotationMatrix.of(angle.negate());
    return tensor -> matrix.dot(tensor);
  }

  @Override // from RigidFamily
  public Tensor forward_se2(Scalar scalar) {
    Scalar angle = function.apply(scalar);
    return Se2Matrix.of(Array.zeros(2).append(angle));
  }
}
