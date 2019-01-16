package ch.ethz.idsc.owl.math.order;

import java.util.Optional;

import junit.framework.TestCase;

public class JassCardsPartialOrderingTest extends TestCase {
  JassCards A = new JassCards(Color.Rosen, CardType.ace, true);
  JassCards B = new JassCards(Color.Rosen, CardType.jack, true);
  JassCards C = new JassCards(Color.Eichel, CardType.queen, false);
  JassCards D = new JassCards(Color.Eichel, CardType.seven, false);
  JassCards E = new JassCards(Color.Schelle, CardType.ace, false);

  public void testAIsTrumpfBIsNot() {
    // since A is Trumpf and B is not: A > B
    Optional<Integer> optional = JassCardsPartialOrdering.INSTANCE.compare(A, C);
    assertTrue(optional.get() == 1);
  }

  public void testAIsNotTrumpfBIs() {
    // since B is Trumpf and A is not: A < B
    Optional<Integer> optional = JassCardsPartialOrdering.INSTANCE.compare(E, B);
    assertTrue(optional.get() == -1);
  }

  public void testAHigherThanBBothTrumpf() {
    // A has higher value and both are Trumpf: A > B
    Optional<Integer> optional = JassCardsPartialOrdering.INSTANCE.compare(A, B);
    assertTrue(optional.get() == 1);
  }

  public void testALowerThanBBothTrumpf() {
    // A has lower value and both are Trumpf: A < B
    Optional<Integer> optional = JassCardsPartialOrdering.INSTANCE.compare(B, A);
    assertTrue(optional.get() == -1);
  }

  public void testAHigherThanBNoneTrumpf() {
    // A has higher value and none are Trumpf: A > B
    Optional<Integer> optional = JassCardsPartialOrdering.INSTANCE.compare(C, D);
    assertTrue(optional.get() == 1);
  }

  public void testALowerThanBNoneTrumpf() {
    // A has lower value and none are Trumpf: A < B
    Optional<Integer> optional = JassCardsPartialOrdering.INSTANCE.compare(D, C);
    assertTrue(optional.get() == -1);
  }

  public void testIncomparable() {
    // Not same color and none is Trumpf: A & B incomparable
    Optional<Integer> optional = JassCardsPartialOrdering.INSTANCE.compare(E, D);
    assertFalse(optional.isPresent());
  }
}