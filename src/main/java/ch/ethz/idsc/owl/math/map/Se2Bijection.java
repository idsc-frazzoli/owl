// code by jph
package ch.ethz.idsc.owl.math.map;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.lie.RotationMatrix;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.Cos;
import ch.ethz.idsc.tensor.sca.Sin;

public class Se2Bijection implements RigidBijection {
  private final Tensor xya;

  /** @param xya == {px, py, angle} */
  public Se2Bijection(Tensor xya) {
    this.xya = xya;
  }

  @Override
  public TensorUnaryOperator forward() {
    return new Se2ForwardAction(xya);
    // Tensor matrix = RotationMatrix.of(xya.Get(2));
    // Tensor offset = xya.extract(0, 2);
    // return tensor -> matrix.dot(tensor).add(offset);
  }

  @Override
  public TensorUnaryOperator inverse() {
    Tensor matrix = RotationMatrix.of(xya.Get(2).negate());
    Tensor offset = xya.extract(0, 2);
    return tensor -> matrix.dot(tensor.subtract(offset));
  }

  @Override
  public Tensor forward_se2() {
    Scalar angle = xya.Get(2);
    Scalar cos = Cos.FUNCTION.apply(angle);
    Scalar sin = Sin.FUNCTION.apply(angle);
    return Tensors.of( //
        Tensors.of(cos, sin.negate(), xya.Get(0)), //
        Tensors.of(sin, cos, xya.Get(1)), //
        Tensors.vector(0, 0, 1));
  }
}
