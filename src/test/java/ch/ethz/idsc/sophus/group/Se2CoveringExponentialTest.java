// code by jph
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
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
}
