// code by ob, jph
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.lie.RotationMatrix;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/** maps from SE(2) to R^2 */
// TODO JPH/OB find official name of this function
/* package */ enum Se2Skew implements TensorUnaryOperator {
  FUNCTION;
  // ---
  @Override
  public Tensor apply(Tensor xya) {
    Scalar angle = xya.Get(2).negate();
    return So2Skew.of(angle).dot(RotationMatrix.of(angle).dot(xya.extract(0, 2)));
  }
}
