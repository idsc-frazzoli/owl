// code by astoll
package ch.ethz.idsc.owl.bot.jass;

import junit.framework.TestCase;

public class JassCardsTest extends TestCase {
  public void testIsTrumpf() {
    JassCard card = new JassCard(JassCard.Color.EICHEL, JassCard.Type.ACE, true);
    assertTrue(card.isTrumpf);
  }

  public void testCheatChecker() {
    JassCard card1 = new JassCard(JassCard.Color.EICHEL, JassCard.Type.ACE, true);
    JassCard card2 = new JassCard(JassCard.Color.SCHELLE, JassCard.Type.ACE, true);
    JassCard card3 = new JassCard(JassCard.Color.SCHELLE, JassCard.Type.ACE, true);
    try {
      card1.cheatChecker(card2);
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      card2.cheatChecker(card3);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testIsGreater() {
    JassCard card1 = new JassCard(JassCard.Color.EICHEL, JassCard.Type.ACE, true);
    JassCard card2 = new JassCard(JassCard.Color.EICHEL, JassCard.Type.KING, true);
    JassCard card3 = new JassCard(JassCard.Color.SCHELLE, JassCard.Type.ACE, false);
    JassCard card4 = new JassCard(JassCard.Color.EICHEL, JassCard.Type.SEVEN, false);
    JassCard card5 = new JassCard(JassCard.Color.SCHELLE, JassCard.Type.ACE, false);
    assertTrue(card1.isGreater(card2));
    assertFalse(card2.isGreater(card1));
    assertTrue(card1.isGreater(card4));
    assertFalse(card3.isGreater(card1));
    assertFalse(card4.isGreater(card5));
    assertFalse(card5.isGreater(card4));
  }
}
