// code by jph
package ch.ethz.idsc.tensor.sca.win;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;
import junit.framework.TestCase;

public class DirichletWindowTest extends TestCase {
  public void testSimple() {
    ScalarUnaryOperator scalarUnaryOperator = DirichletWindow.FUNCTION;
    Scalar s0 = scalarUnaryOperator.apply(RealScalar.of(.1));
    assertEquals(s0, RealScalar.ONE);
    Scalar s1 = scalarUnaryOperator.apply(RealScalar.of(.6));
    assertEquals(s1, RealScalar.ZERO);
  }

  public void testQuantityFail() {
    try {
      DirichletWindow.FUNCTION.apply(Quantity.of(2, "s"));
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
  }
}
