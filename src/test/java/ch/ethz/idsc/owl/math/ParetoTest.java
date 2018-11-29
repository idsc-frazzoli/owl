// code by ynager
package ch.ethz.idsc.owl.math;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class ParetoTest extends TestCase {
  public void testSimple() {
    Tensor a = Tensors.vector(1, 1);
    Tensor b = Tensors.vector(2, 2);
    Tensor c = Tensors.vector(1, 2);
    assertTrue(Pareto.isDominated(a, b));
    assertFalse(Pareto.isDominated(b, a));
    assertFalse(Pareto.isDominated(a, c));
  }

  public void testFail() {
    try {
      Tensor a = Tensors.vector(1, 1);
      Tensor b = Tensors.vector(2, 2, 3);
      Pareto.isDominated(a, b);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
