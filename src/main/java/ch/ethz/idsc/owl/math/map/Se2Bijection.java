// code by jph
package ch.ethz.idsc.owl.math.map;

import java.io.Serializable;

import ch.ethz.idsc.sophus.lie.se2.Se2Utils;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/** Se2Bijection forward
 * SE2 matrix dot point
 * 
 * Se2Bijection inverse
 * (SE2 matrix)^-1 dot point
 * 
 * @see Se2InverseAction */
public class Se2Bijection implements RigidBijection, Serializable {
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
