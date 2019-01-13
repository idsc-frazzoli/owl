// code by jph
package ch.ethz.idsc.sophus.planar;

import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.UnitVector;
import junit.framework.TestCase;

public class Det2DTest extends TestCase {
  public void testSimple() {
    assertEquals(Det2D.of(Tensors.vector(1, 0), Tensors.vector(0, 1)), RealScalar.ONE);
    assertEquals(Det2D.of(Tensors.vector(1, 1), Tensors.vector(0, 1)), RealScalar.ONE);
    assertEquals(Det2D.of(Tensors.vector(1, 2), Tensors.vector(0, 1)), RealScalar.ONE);
  }

  public void testMore() {
    assertEquals(Det2D.of(Tensors.vector(0, 1), Tensors.vector(1, 1)), RealScalar.ONE.negate());
  }

  public void testArea() {
    Scalar det = Det2D.of(UnitVector.of(2, 0), UnitVector.of(2, 1));
    assertEquals(det, RealScalar.ONE);
    assertTrue(ExactScalarQ.of(det));
  }

  public void testFailP() {
    try {
      Det2D.of(Tensors.vector(1, 0), Tensors.vector(0, 1, 0));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testFailQ() {
    try {
      Det2D.of(Tensors.vector(1, 0, 0), Tensors.vector(0, 1));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
