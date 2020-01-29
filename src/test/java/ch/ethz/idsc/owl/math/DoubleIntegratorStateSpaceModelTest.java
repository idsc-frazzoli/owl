// code by jph
package ch.ethz.idsc.owl.math;

import ch.ethz.idsc.owl.math.model.DoubleIntegratorStateSpaceModel;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class DoubleIntegratorStateSpaceModelTest extends TestCase {
  public void testSimple() {
    Tensor x = Tensors.vector(9, 8, 1, 2);
    Tensor u = Tensors.vector(3, 4);
    Tensor r = DoubleIntegratorStateSpaceModel.INSTANCE.f(x, u);
    assertEquals(r, Tensors.vector(1, 2, 3, 4));
  }

  public void testFail() {
    Tensor x = Tensors.vector(1, 2, 3, 4);
    try {
      DoubleIntegratorStateSpaceModel.INSTANCE.f(x, Tensors.vector(3));
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      DoubleIntegratorStateSpaceModel.INSTANCE.f(x, Tensors.vector(3, 4, 3));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
