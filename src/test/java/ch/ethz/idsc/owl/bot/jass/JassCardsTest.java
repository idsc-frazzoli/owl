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
}
