// code by jph
package ch.ethz.idsc.sophus.hs.r2;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.Cos;
import ch.ethz.idsc.tensor.sca.Sin;

/** Se2ForwardAction is a substitute for the operation:
 * SE2 matrix dot point
 * 
 * Se2ForwardAction is the canonic action of SE2 on R^2.
 * 
 * @see Se2InverseAction */
/* package */ class Se2ForwardAction implements TensorUnaryOperator {
  private final Scalar px;
  private final Scalar py;
  private final Scalar ca;
  private final Scalar sa;

  public Se2ForwardAction(Tensor xya) {
    px = xya.Get(0);
    py = xya.Get(1);
    Scalar angle = xya.Get(2);
    ca = Cos.FUNCTION.apply(angle);
    sa = Sin.FUNCTION.apply(angle);
  }

  @Override // from TensorUnaryOperator
  public Tensor apply(Tensor tensor) {
    Scalar qx = tensor.Get(0);
    Scalar qy = tensor.Get(1);
    return Tensors.of( //
        px.add(qx.multiply(ca)).subtract(qy.multiply(sa)), //
        py.add(qx.multiply(sa)).add(qy.multiply(ca)) //
    );
  }
}
