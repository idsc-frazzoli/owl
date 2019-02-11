// code by astoll
package ch.ethz.idsc.owl.bot.jass;

import ch.ethz.idsc.owl.bot.jass.JassCard.Color;
import ch.ethz.idsc.owl.bot.jass.JassCard.Type;
import junit.framework.TestCase;

public class JassCardTest extends TestCase {
  public void testIsTrumpf() {
    JassCard card = new JassCard(Color.EICHEL, Type.ACE, true);
    assertTrue(card.isTrumpf());
  }

  public void testCheatChecker() {
    JassCard card1 = new JassCard(Color.EICHEL, Type.ACE, true);
    JassCard card2 = new JassCard(Color.SCHELLE, Type.ACE, true);
    JassCard card3 = new JassCard(Color.SCHELLE, Type.ACE, true);
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

  public void testIsLess() {
    JassCard card1 = new JassCard(Color.EICHEL, Type.ACE, true);
    JassCard card2 = new JassCard(Color.EICHEL, Type.JACK, true);
    JassCard card3 = new JassCard(Color.SCHELLE, Type.ACE, false);
    JassCard card4 = new JassCard(Color.EICHEL, Type.SEVEN, false);
    JassCard card5 = new JassCard(Color.SCHELLE, Type.ACE, false);
    assertFalse(card2.isLess(card1));
    assertTrue(card1.isLess(card2));
    assertTrue(card4.isLess(card1));
    assertFalse(card1.isLess(card3));
    assertFalse(card5.isLess(card4));
    assertFalse(card4.isLess(card5));
  }

  public void testEquals() {
    JassCard card1 = new JassCard(Color.EICHEL, Type.ACE, true);
    JassCard card2 = new JassCard(Color.EICHEL, Type.ACE, true);
    JassCard card3 = new JassCard(Color.EICHEL, Type.JACK, true);
    assertTrue(card1.equals(card2));
    assertFalse(card2.equals(card3));
  }

  public void testHashCode() {
    JassCard card1 = new JassCard(Color.EICHEL, Type.ACE, true);
    JassCard card2 = new JassCard(Color.EICHEL, Type.ACE, true);
    assertTrue(card1.hashCode() == card2.hashCode());
    JassCard card3 = new JassCard(Color.EICHEL, Type.JACK, true);
    assertFalse(card1.hashCode() == card3.hashCode());
  }
}
