// code by jph
package ch.ethz.idsc.owl.glc.adapter;

import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class SimpleRelabelDecisionTest extends TestCase {
  private static boolean doRelabel(Scalar newMerit, Scalar oldMerit, Scalar slack) {
    return ((SimpleRelabelDecision) SimpleRelabelDecision.with(slack)).doRelabel(newMerit, oldMerit);
  }

  private static void check(boolean a, boolean b, boolean c) {
    boolean r1 = a || (b && c);
    boolean r2 = a || b && c;
    assertEquals(r1, r2);
  }

  public void testSimple() {
    check(true, true, true);
    check(true, true, false);
    check(true, false, true);
    check(true, false, false);
    check(false, true, true);
    check(false, true, false);
    check(false, false, true);
    check(false, false, false);
  }

  public void testStatic() {
    assertTrue(doRelabel(RealScalar.of(1), RealScalar.of(2), DoubleScalar.of(2)));
    assertFalse(doRelabel(RealScalar.of(3), RealScalar.of(2), DoubleScalar.of(2)));
    assertFalse(doRelabel(RealScalar.of(1.), RealScalar.of(2), DoubleScalar.of(2)));
    assertTrue(doRelabel(RealScalar.of(1.), RealScalar.of(2), DoubleScalar.of(0.5)));
    assertFalse(doRelabel(RealScalar.of(1.9), RealScalar.of(2), DoubleScalar.of(0.5)));
    assertFalse(doRelabel(RealScalar.of(2.1), RealScalar.of(2), DoubleScalar.of(0.5)));
  }

  public void testQuantity() {
    assertTrue(doRelabel(Quantity.of(1, "USD"), Quantity.of(2, "USD"), DoubleScalar.of(0.01)));
    assertFalse(doRelabel(Quantity.of(3, "USD"), Quantity.of(2, "USD"), DoubleScalar.of(0.01)));
  }

  public void testChop() {
    Chop._05.requireZero(Quantity.of(1e-7, "USD"));
  }
}
