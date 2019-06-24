// code by astoll
package ch.ethz.idsc.owl.demo.order;

import java.util.Objects;

/** Creates a card of the game jassen with the attributes color and card type
 * 
 * https://en.wikipedia.org/wiki/Jass
 * 
 * @author astoll */
public class JassCard {
  public static enum Color {
    ROSEN, EICHEL, SCHILTEN, SCHELLE
  }

  public static enum Type {
    SIX(1), SEVEN(2), EIGHT(3), NINE(8), TEN(4), JACK(9), QUEEN(5), KING(6), ACE(7),;
    private final int trumpfOrdering;

    private Type(int trumpfOrdering) {
      this.trumpfOrdering = trumpfOrdering;
    }
  }

  private final Color color;
  private final Type type;
  private final boolean isTrumpf;

  public JassCard(Color color, Type type, boolean isTrumpf) {
    this.color = color;
    this.type = type;
    this.isTrumpf = isTrumpf;
  }

  public boolean isTrumpf() {
    return isTrumpf;
  }

  /** checks whether the two cards are exactly the same or if two different colors were assigned Trumpf
   * 
   * @param jassCard to check for eligibility
   * @throws RunTimeException */
  public void cheatChecker(JassCard jassCard) {
    if (this.equals(jassCard)) {
      System.err.println("There are no duplicate cards in a deck!");
      throw new RuntimeException("cards not from the same deck");
    }
    if (this.isTrumpf && jassCard.isTrumpf && !this.color.equals(jassCard.color)) {
      System.err.println("Only one card color can be assigned Trumpf!");
      throw new RuntimeException("cards not from the same deck");
    }
  }

  /** checks whether the card is less (worse) than another card.
   * 
   * @param jassCard to be compared to
   * @return true if this card is less (according to the rules) than the given jassCard, false otherwise */
  public boolean isLess(JassCard jassCard) {
    cheatChecker(jassCard);
    if (isTrumpf && jassCard.isTrumpf)
      if (type.trumpfOrdering < jassCard.type.trumpfOrdering)
        return true;
    if (!this.isTrumpf && jassCard.isTrumpf)
      return true;
    if (!isTrumpf && !jassCard.isTrumpf)
      if (color.equals(jassCard.color) && type.compareTo(jassCard.type) < 0)
        return true;
    return false;
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof JassCard) {
      JassCard jassCard = (JassCard) object;
      return color.equals(jassCard.color) //
          && type.equals(jassCard.type) //
          && isTrumpf == jassCard.isTrumpf;
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(color, type, isTrumpf);
  }
}
