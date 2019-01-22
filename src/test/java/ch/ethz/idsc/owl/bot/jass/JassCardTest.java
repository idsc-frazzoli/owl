// code by astoll
package ch.ethz.idsc.owl.bot.jass;

import junit.framework.TestCase;

public class JassCardTest extends TestCase {
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
    assertTrue(card2.isLess(card1));
    assertFalse(card1.isLess(card2));
    assertTrue(card4.isLess(card1));
    assertFalse(card1.isLess(card3));
    assertFalse(card5.isLess(card4));
    assertFalse(card4.isLess(card5));
  }
}
