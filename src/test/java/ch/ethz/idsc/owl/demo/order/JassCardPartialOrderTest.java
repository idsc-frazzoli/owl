// code by astoll
package ch.ethz.idsc.owl.demo.order;

import ch.ethz.idsc.owl.demo.order.JassCard.Farbe;
import ch.ethz.idsc.owl.demo.order.JassCard.Type;
import ch.ethz.idsc.owl.math.AssertFail;
import ch.ethz.idsc.owl.math.order.OrderComparison;
import junit.framework.TestCase;

public class JassCardPartialOrderTest extends TestCase {
  final JassCard A = new JassCard(Farbe.ROSEN, Type.ACE, true);
  final JassCard B = new JassCard(Farbe.ROSEN, Type.JACK, true);
  final JassCard C = new JassCard(Farbe.EICHEL, Type.QUEEN, false);
  final JassCard D = new JassCard(Farbe.EICHEL, Type.SEVEN, false);
  final JassCard E = new JassCard(Farbe.SCHELLE, Type.ACE, false);

  public void testAIsTrumpfBIsNot() {
    // since A is Trumpf and B is not: A > B
    assertEquals(JassCardPartialOrder.INSTANCE.compare(A, C), OrderComparison.STRICTLY_SUCCEEDS);
  }

  public void testAIsNotTrumpfBIs() {
    // since B is Trumpf and A is not: A < B
    assertEquals(JassCardPartialOrder.INSTANCE.compare(C, B), OrderComparison.STRICTLY_PRECEDES);
  }

  public void testAHigherThanBBothTrumpf() {
    // A has higher value and both are Trumpf: A > B
    assertEquals(JassCardPartialOrder.INSTANCE.compare(B, A), OrderComparison.STRICTLY_SUCCEEDS);
  }

  public void testALowerThanBBothTrumpf() {
    // A has lower value and both are Trumpf: A < B
    assertEquals(JassCardPartialOrder.INSTANCE.compare(A, B), OrderComparison.STRICTLY_PRECEDES);
  }

  public void testAHigherThanBNoneTrumpf() {
    // A has higher value and none are Trumpf: A > B
    assertEquals(JassCardPartialOrder.INSTANCE.compare(C, D), OrderComparison.STRICTLY_SUCCEEDS);
  }

  public void testALowerThanBNoneTrumpf() {
    // A has lower value and none are Trumpf: A < B
    assertEquals(JassCardPartialOrder.INSTANCE.compare(D, C), OrderComparison.STRICTLY_PRECEDES);
  }

  public void testIncomparable() {
    // Not same color and none is Trumpf: D & E incomparable
    assertEquals(JassCardPartialOrder.INSTANCE.compare(D, E), OrderComparison.INCOMPARABLE);
  }

  public void testFail() {
    AssertFail.of(() -> JassCardPartialOrder.INSTANCE.compare(D, D));
  }
}