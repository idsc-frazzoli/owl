// code by jph
package ch.ethz.idsc.owl.math.map;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.Cos;
import ch.ethz.idsc.tensor.sca.Sin;

/** Se2InverseAction is a substitute for the operation:
 * Inverse[SE2 matrix] dot point */
/* package */ class Se2InverseAction implements TensorUnaryOperator {
  private final Scalar px;
  private final Scalar py;
  private final Scalar ca;
  private final Scalar sa;

  public Se2InverseAction(Tensor xya) {
    px = xya.Get(0);
    py = xya.Get(1);
    Scalar angle = xya.Get(2).negate();
    ca = Cos.FUNCTION.apply(angle);
    sa = Sin.FUNCTION.apply(angle);
  }

  @Override // from TensorUnaryOperator
  public Tensor apply(Tensor tensor) {
    Scalar qx = tensor.Get(0).subtract(px);
    Scalar qy = tensor.Get(1).subtract(py);
    return Tensors.of( //
        qx.multiply(ca).subtract(qy.multiply(sa)), //
        qx.multiply(sa).add(qy.multiply(ca)) //
    );
  }
}
