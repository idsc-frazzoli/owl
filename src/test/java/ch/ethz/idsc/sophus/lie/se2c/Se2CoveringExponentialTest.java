// code by jph
package ch.ethz.idsc.sophus.lie.se2c;

import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class Se2CoveringExponentialTest extends TestCase {
  public void testSimpleXY() {
    Tensor x = Tensors.vector(3, 2, 0).unmodifiable();
    Tensor g = Se2CoveringExponential.INSTANCE.exp(x);
    ExactTensorQ.require(g);
    assertEquals(g, x);
    Tensor y = Se2CoveringExponential.INSTANCE.log(g);
    ExactTensorQ.require(y);
    assertEquals(y, x);
  }

  public void testSimpleLinearSubspace() {
    for (int theta = -10; theta <= 10; ++theta) {
      Tensor x = Tensors.vector(0, 0, theta).unmodifiable();
      Tensor g = Se2CoveringExponential.INSTANCE.exp(x);
      assertEquals(g, x);
      Tensor y = Se2CoveringExponential.INSTANCE.log(g);
      assertEquals(y, x);
    }
  }

  public void testQuantity() {
    Tensor xya = Tensors.fromString("{1[m], 2[m], 0.3}");
    Tensor log = Se2CoveringExponential.INSTANCE.log(xya);
    Chop._12.requireClose(log, Tensors.fromString("{1.2924887258384925[m], 1.834977451676985[m], 0.3}"));
    Tensor exp = Se2CoveringExponential.INSTANCE.exp(log);
    Chop._12.requireClose(exp, xya);
  }
}
