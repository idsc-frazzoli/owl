package ch.ethz.idsc.owl.math.order;

import junit.framework.TestCase;

public class JassCardsTest extends TestCase {
  public void testSimple() {
    JassCards card = new JassCards(Color.Eichel, CardType.ace, true);
    assertTrue(card.isTrumpf);
  }

  public void testCheatChecker() {
    JassCards card1 = new JassCards(Color.Eichel, CardType.ace, true);
    JassCards card2 = new JassCards(Color.Schelle, CardType.ace, true);
    JassCards card3 = new JassCards(Color.Schelle, CardType.ace, true);
    try {
      card1.CheatChecker(card2);
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      card2.CheatChecker(card3);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
