// code by astoll
package ch.ethz.idsc.owl.bot.jass;

// TODO outsource
/** Creates a card of the game jassen with the attributes color and card type
 * 
 * @author astoll */
/* package */ class JassCard {
  static enum Color {
    ROSEN, EICHEL, SCHILTEN, SCHELLE
  }

  static enum Type {
    SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING, ACE
  }

  final Color color;
  final Type type;
  final boolean isTrumpf;

  JassCard(Color color, Type type, boolean isTrumpf) {
    this.color = color;
    this.type = type;
    this.isTrumpf = isTrumpf;
  }

  /** checks whether the two cards are exactly the same or if two different colors were assigned Trumpf
   * 
   * @param jassCard to check for eligibility
   * @throws RunTimeException */
  public void cheatChecker(JassCard jassCard) {
    if (color.equals(jassCard.color) && type.equals(jassCard.type)) {
      System.err.println("There are no duplicate cards in a deck!");
      throw new RuntimeException("cards not from the same deck");
    }
    if (isTrumpf && jassCard.isTrumpf && !color.equals(jassCard.color)) {
      System.err.println("Only card color can be assigned Trumpf!");
      throw new RuntimeException("cards not from the same deck");
    }
  }

  /** checks whether the card is greater (better) than another card.
   * 
   * @param jassCard to be compared to
   * @return true if this card is greater (according to the rules) than the given jassCard, false otherwise */
  public boolean isGreater(JassCard jassCard) {
    cheatChecker(jassCard);
    if (isTrumpf)
      if (jassCard.isTrumpf) {
        if (0 < type.compareTo(jassCard.type))
          return true;
      } else
        return true;
    return !jassCard.isTrumpf && color.equals(jassCard.color) && 0 < type.compareTo(jassCard.type);
  }
}
