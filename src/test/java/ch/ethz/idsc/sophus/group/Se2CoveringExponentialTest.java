// code by jph
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class Se2CoveringExponentialTest extends TestCase {
  public void testSimpleXY() {
    Tensor x = Tensors.vector(3, 2, 0).unmodifiable();
    Tensor g = Se2CoveringExponential.INSTANCE.exp(x);
    assertTrue(ExactScalarQ.all(g));
    assertEquals(g, x);
    Tensor y = Se2CoveringExponential.INSTANCE.log(g);
    assertTrue(ExactScalarQ.all(y));
    assertEquals(y, x);
  }

  public void testSimpleTheta() {
    Tensor x = Tensors.vector(0, 0, -2).unmodifiable();
    Tensor g = Se2CoveringExponential.INSTANCE.exp(x);
    assertEquals(g, x);
    Tensor y = Se2CoveringExponential.INSTANCE.log(g);
    assertEquals(y, x);
  }
}
