// code by astoll
package ch.ethz.idsc.owl.math.order;

import ch.ethz.idsc.owl.bot.jass.JassCard;
import ch.ethz.idsc.owl.bot.jass.JassCard.Color;
import ch.ethz.idsc.owl.bot.jass.JassCard.Type;
import ch.ethz.idsc.owl.bot.jass.JassCardPartialComparator;
import junit.framework.TestCase;

public class StrictPartialOrderMinTrackerTest extends TestCase {
  public void testDigestNotEmptyList() {
    StrictPartialComparator<JassCard> strictPartialComparator = JassCardPartialComparator.INSTANCE;
    StrictPartialOrderMinTracker<JassCard> jassCard = StrictPartialOrderMinTracker.withList(strictPartialComparator);
    JassCard card1 = new JassCard(Color.EICHEL, Type.ACE, true);
    jassCard.digest(card1);
    assertFalse(jassCard.getMinElements().isEmpty());
    JassCard card2 = new JassCard(Color.SCHELLE, Type.JACK, false);
    JassCard card3 = new JassCard(Color.SCHILTEN, Type.ACE, false);
    JassCard card4 = new JassCard(Color.ROSEN, Type.SIX, false);
    jassCard.digest(card2);
    jassCard.digest(card3);
    jassCard.digest(card4);
    assertTrue(jassCard.getMinElements().contains(card2));
    assertTrue(jassCard.getMinElements().contains(card3));
    assertTrue(jassCard.getMinElements().contains(card4));
  }

  public void testDigestNotEmptySet() {
    StrictPartialComparator<JassCard> strictPartialComparator = JassCardPartialComparator.INSTANCE;
    StrictPartialOrderMinTracker<JassCard> jassCard = StrictPartialOrderMinTracker.withSet(strictPartialComparator);
    JassCard card1 = new JassCard(Color.EICHEL, Type.ACE, true);
    jassCard.digest(card1);
    assertFalse(jassCard.getMinElements().isEmpty());
    JassCard card2 = new JassCard(Color.SCHELLE, Type.JACK, false);
    JassCard card3 = new JassCard(Color.SCHILTEN, Type.ACE, false);
    JassCard card4 = new JassCard(Color.ROSEN, Type.SIX, false);
    jassCard.digest(card2);
    jassCard.digest(card3);
    jassCard.digest(card4);
    assertTrue(jassCard.getMinElements().contains(card2));
    assertTrue(jassCard.getMinElements().contains(card3));
    assertTrue(jassCard.getMinElements().contains(card4));
  }
}
