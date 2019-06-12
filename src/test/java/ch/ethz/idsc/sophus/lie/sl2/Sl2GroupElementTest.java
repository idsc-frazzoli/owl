// code by jph
package ch.ethz.idsc.sophus.lie.sl2;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.UnitVector;
import junit.framework.TestCase;

public class Sl2GroupElementTest extends TestCase {
  public void testSimple() {
    Tensor vector = Tensors.vector(-2, 7, 3);
    Sl2GroupElement sl2GroupElement = new Sl2GroupElement(vector);
    Sl2GroupElement inverse = sl2GroupElement.inverse();
    assertEquals(inverse.vector(), Tensors.fromString("{2/3, -7/3, 1/3}"));
    assertEquals(inverse.combine(vector), UnitVector.of(3, 2));
  }

  public void testFailZero() {
    try {
      new Sl2GroupElement(Tensors.vector(1, 2, 0));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
