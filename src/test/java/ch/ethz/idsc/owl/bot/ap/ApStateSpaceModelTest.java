//code by andre
package ch.ethz.idsc.owl.bot.ap;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class ApStateSpaceModelTest extends TestCase {
  public void testSimple() {
    Tensor f = ApStateSpaceModel.INSTANCE.f(Tensors.vector(80, 50, 30, 0.1), Tensors.vector(0, 0));
    assertEquals(4,f.length());
  }
}
