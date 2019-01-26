// code by astoll
package ch.ethz.idsc.owl.bot.jass;

import ch.ethz.idsc.owl.bot.jass.JassCard.Color;
import ch.ethz.idsc.owl.bot.jass.JassCard.Type;
import ch.ethz.idsc.owl.math.order.StrictPartialComparison;
import junit.framework.TestCase;

public class JassCardPartialComparatorTest extends TestCase {
  final JassCard A = new JassCard(Color.ROSEN, Type.ACE, true);
  final JassCard B = new JassCard(Color.ROSEN, Type.JACK, true);
  final JassCard C = new JassCard(Color.EICHEL, Type.QUEEN, false);
  final JassCard D = new JassCard(Color.EICHEL, Type.SEVEN, false);
  final JassCard E = new JassCard(Color.SCHELLE, Type.ACE, false);

  public void testAIsTrumpfBIsNot() {
    // since A is Trumpf and B is not: A > B
    assertEquals(JassCardPartialComparator.INSTANCE.compare(A, C), StrictPartialComparison.GREATER_THAN);
  }

  public void testAIsNotTrumpfBIs() {
    // since B is Trumpf and A is not: A < B
    assertEquals(JassCardPartialComparator.INSTANCE.compare(C, B), StrictPartialComparison.LESS_THAN);
  }

  public void testAHigherThanBBothTrumpf() {
    // A has higher value and both are Trumpf: A > B
    assertEquals(JassCardPartialComparator.INSTANCE.compare(B, A), StrictPartialComparison.GREATER_THAN);
  }

  public void testALowerThanBBothTrumpf() {
    // A has lower value and both are Trumpf: A < B
    assertEquals(JassCardPartialComparator.INSTANCE.compare(A, B), StrictPartialComparison.LESS_THAN);
  }

  public void testAHigherThanBNoneTrumpf() {
    // A has higher value and none are Trumpf: A > B
    assertEquals(JassCardPartialComparator.INSTANCE.compare(C, D), StrictPartialComparison.GREATER_THAN);
  }

  public void testALowerThanBNoneTrumpf() {
    // A has lower value and none are Trumpf: A < B
    assertEquals(JassCardPartialComparator.INSTANCE.compare(D, C), StrictPartialComparison.LESS_THAN);
  }

  public void testIncomparable() {
    // Not same color and none is Trumpf: D & E incomparable
    assertEquals(JassCardPartialComparator.INSTANCE.compare(D, E), StrictPartialComparison.INCOMPARABLE);
  }

  public void testEqualsFail() {
    try {
      JassCardPartialComparator.INSTANCE.compare(D, D);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}