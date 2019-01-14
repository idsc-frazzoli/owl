// code by jph
package ch.ethz.idsc.owl.math.planar;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class Extract2DTest extends TestCase {
  public void testSimple() {
    assertEquals(Extract2D.FUNCTION.apply(Tensors.vector(1, 2, 4)), Tensors.vector(1, 2));
  }

  public void testFailScalar() {
    try {
      Extract2D.FUNCTION.apply(RealScalar.ONE);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testFailEmpty() {
    try {
      Extract2D.FUNCTION.apply(Tensors.empty());
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testFailOne() {
    try {
      Extract2D.FUNCTION.apply(Tensors.vector(1));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
