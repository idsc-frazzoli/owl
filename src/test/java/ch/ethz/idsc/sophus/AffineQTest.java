// code by jph
package ch.ethz.idsc.sophus;

import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class AffineQTest extends TestCase {
  public void testSimple() {
    AffineQ.requirePositive(Tensors.vector(0.5, 0.5));
    AffineQ.requirePositive(Tensors.vector(0.25, 0.25, 0.25, 0.25));
    AffineQ.requirePositive(Tensors.vector(1, 0));
  }

  public void testFail() {
    try {
      AffineQ.requirePositive(Tensors.vector(2, -1));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testFail2() {
    try {
      AffineQ.requirePositive(Tensors.vector(1, 1));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
