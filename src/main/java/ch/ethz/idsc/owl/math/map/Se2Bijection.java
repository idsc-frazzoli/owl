// code by jph
package ch.ethz.idsc.owl.math.map;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

public class Se2Bijection implements RigidBijection {
  private final Tensor xya;

  /** @param xya == {px, py, angle} as member of Lie group SE2 */
  public Se2Bijection(Tensor xya) {
    this.xya = xya;
  }

  @Override // from Bijection
  public TensorUnaryOperator forward() {
    return new Se2ForwardAction(xya);
  }

  @Override // from Bijection
  public TensorUnaryOperator inverse() {
    return new Se2InverseAction(xya);
  }

  @Override // from RigidBijection
  public Tensor forward_se2() {
    return Se2Utils.toSE2Matrix(xya);
  }
}
