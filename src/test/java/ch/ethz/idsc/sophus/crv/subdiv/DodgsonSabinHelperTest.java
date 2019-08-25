// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class DodgsonSabinHelperTest extends TestCase {
  public void testCrafted() {
    Tensor a = Tensors.vector(1, 1.1);
    Tensor b = Tensors.vector(2.2, 0.5);
    Tensor c = Tensors.vector(3, 1.5);
    Tensor d = Tensors.vector(3.5, 2.9);
    Tensor tensor = DodgsonSabinHelper.midpoint(a, b, c, d);
    Chop._13.requireClose(tensor, Tensors.fromString("{2.64619661516195, 0.8388990046231528}"));
  }

  public void testCraftedReverse() {
    Tensor d = Tensors.vector(1, 1.1);
    Tensor c = Tensors.vector(2.2, 0.5);
    Tensor b = Tensors.vector(3, 1.5);
    Tensor a = Tensors.vector(3.5, 2.9);
    Tensor tensor = DodgsonSabinHelper.midpoint(a, b, c, d);
    Chop._13.requireClose(tensor, Tensors.fromString("{2.64619661516195, 0.8388990046231528}"));
  }

  public void testLine() {
    Tensor a = Tensors.vector(1, 1.1);
    Tensor b = Tensors.vector(2, 1.1);
    Tensor c = Tensors.vector(3, 1.1);
    Tensor d = Tensors.vector(4, 1.1);
    Tensor tensor = DodgsonSabinHelper.midpoint(a, b, c, d);
    assertEquals(tensor, Tensors.vector(2.5, 1.1));
  }

  public void testLineDiagonal() {
    Tensor a = Tensors.vector(1, 1.1);
    Tensor b = Tensors.vector(2, 2.1);
    Tensor c = Tensors.vector(3, 3.1);
    Tensor d = Tensors.vector(4, 4.1);
    Tensor tensor = DodgsonSabinHelper.midpoint(a, b, c, d);
    assertEquals(tensor, Tensors.vector(2.5, 2.6));
  }

  public void testLineThree() {
    Tensor a = Tensors.vector(1, 1.1);
    Tensor b = Tensors.vector(2, 1.1);
    Tensor c = Tensors.vector(3, 1.1);
    Tensor tensor = DodgsonSabinHelper.midpoint(a, b, c);
    assertEquals(tensor, Tensors.vector(1.5, 1.1));
  }
}
