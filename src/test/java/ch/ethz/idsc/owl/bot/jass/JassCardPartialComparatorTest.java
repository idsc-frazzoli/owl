// code by astoll
package ch.ethz.idsc.owl.bot.jass;

import ch.ethz.idsc.owl.math.order.StrictPartialComparison;
import junit.framework.TestCase;

public class JassCardPartialComparatorTest extends TestCase {
  JassCard A = new JassCard(JassCard.Color.ROSEN, JassCard.Type.ACE, true);
  JassCard B = new JassCard(JassCard.Color.ROSEN, JassCard.Type.JACK, true);
  JassCard C = new JassCard(JassCard.Color.EICHEL, JassCard.Type.QUEEN, false);
  JassCard D = new JassCard(JassCard.Color.EICHEL, JassCard.Type.SEVEN, false);
  JassCard E = new JassCard(JassCard.Color.SCHELLE, JassCard.Type.ACE, false);
  JassCard F = new JassCard(JassCard.Color.SCHELLE, JassCard.Type.ACE, false);

  public void testAIsTrumpfBIsNot() {
    // since A is Trumpf and B is not: A > B
    StrictPartialComparison optional = JassCardPartialComparator.INSTANCE.compare(A, C);
    assertEquals(optional, StrictPartialComparison.GREATER_THAN);
  }

  public void testAIsNotTrumpfBIs() {
    // since B is Trumpf and A is not: A < B
    StrictPartialComparison optional = JassCardPartialComparator.INSTANCE.compare(E, B);
    assertEquals(optional, StrictPartialComparison.LESS_THAN);
  }

  public void testAHigherThanBBothTrumpf() {
    // A has higher value and both are Trumpf: A > B
    StrictPartialComparison optional = JassCardPartialComparator.INSTANCE.compare(A, B);
    assertEquals(optional, StrictPartialComparison.GREATER_THAN);
  }

  public void testALowerThanBBothTrumpf() {
    // A has lower value and both are Trumpf: A < B
    StrictPartialComparison optional = JassCardPartialComparator.INSTANCE.compare(B, A);
    assertEquals(optional, StrictPartialComparison.LESS_THAN);
  }

  public void testAHigherThanBNoneTrumpf() {
    // A has higher value and none are Trumpf: A > B
    StrictPartialComparison optional = JassCardPartialComparator.INSTANCE.compare(C, D);
    assertEquals(optional, StrictPartialComparison.GREATER_THAN);
  }

  public void testALowerThanBNoneTrumpf() {
    // A has lower value and none are Trumpf: A < B
    StrictPartialComparison optional = JassCardPartialComparator.INSTANCE.compare(D, C);
    assertEquals(optional, StrictPartialComparison.LESS_THAN);
  }

  public void testIncomparable() {
    // Not same color and none is Trumpf: A & B incomparable
    StrictPartialComparison optional = JassCardPartialComparator.INSTANCE.compare(E, D);
    assertEquals(optional, StrictPartialComparison.INCOMPARABLE);
  }
}