// code by astoll
package ch.ethz.idsc.owl.demo.order;

import ch.ethz.idsc.owl.demo.order.JassCard.Farbe;
import ch.ethz.idsc.owl.demo.order.JassCard.Type;
import ch.ethz.idsc.owl.math.AssertFail;
import junit.framework.TestCase;

public class JassCardTest extends TestCase {
  public void testIsTrumpf() {
    JassCard card = new JassCard(Farbe.EICHEL, Type.ACE, true);
    assertTrue(card.isTrumpf());
  }

  public void testCheatChecker() {
    JassCard card1 = new JassCard(Farbe.EICHEL, Type.ACE, true);
    JassCard card2 = new JassCard(Farbe.SCHELLE, Type.ACE, true);
    JassCard card3 = new JassCard(Farbe.SCHELLE, Type.ACE, true);
    AssertFail.of(() -> card1.cheatChecker(card2));
    AssertFail.of(() -> card2.cheatChecker(card3));
  }

  public void testIsLess() {
    JassCard card1 = new JassCard(Farbe.EICHEL, Type.ACE, true);
    JassCard card2 = new JassCard(Farbe.EICHEL, Type.JACK, true);
    JassCard card3 = new JassCard(Farbe.SCHELLE, Type.ACE, false);
    JassCard card4 = new JassCard(Farbe.EICHEL, Type.SEVEN, false);
    JassCard card5 = new JassCard(Farbe.SCHELLE, Type.ACE, false);
    assertFalse(card2.isLess(card1));
    assertTrue(card1.isLess(card2));
    assertTrue(card4.isLess(card1));
    assertFalse(card1.isLess(card3));
    assertFalse(card5.isLess(card4));
    assertFalse(card4.isLess(card5));
  }

  public void testEquals() {
    JassCard card1 = new JassCard(Farbe.EICHEL, Type.ACE, true);
    JassCard card2 = new JassCard(Farbe.EICHEL, Type.ACE, true);
    JassCard card3 = new JassCard(Farbe.EICHEL, Type.JACK, true);
    assertTrue(card1.equals(card2));
    assertFalse(card2.equals(card3));
  }

  public void testHashCode() {
    JassCard card1 = new JassCard(Farbe.EICHEL, Type.ACE, true);
    JassCard card2 = new JassCard(Farbe.EICHEL, Type.ACE, true);
    assertTrue(card1.hashCode() == card2.hashCode());
    JassCard card3 = new JassCard(Farbe.EICHEL, Type.JACK, true);
    assertFalse(card1.hashCode() == card3.hashCode());
  }
}
