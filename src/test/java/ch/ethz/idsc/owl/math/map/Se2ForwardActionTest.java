// code by jph
package ch.ethz.idsc.owl.math.map;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class Se2ForwardActionTest extends TestCase {
  public void testSimple() {
    Tensor xya = Tensors.vector(1, 2, 3);
    TensorUnaryOperator tuo = new Se2ForwardAction(xya);
    Tensor p = Tensors.vector(6, -9, 1);
    Tensor q1 = tuo.apply(p);
    Tensor m = Se2Utils.toSE2Matrix(xya);
    Tensor q2 = m.dot(p).extract(0, 2);
    assertTrue(Chop._12.close(q1, q2));
  }
}
