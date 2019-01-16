// code by astoll
package ch.ethz.idsc.owl.math.order;

//TODO outsource
enum Color {
  Rosen, Eichel, Schilten, Schelle
}

enum CardType {
  six, seven, eight, nine, ten, jack, queen, king, ace
}

/** Creates a card of the game jassen with the attributes color and card type
 * 
 * @author astoll */
/* package */ class JassCards {
  Color color;
  CardType cardType;
  boolean isTrumpf;

  protected JassCards(Color color, CardType cardType, boolean isTrumpf) {
    this.color = color;
    this.cardType = cardType;
    this.isTrumpf = isTrumpf;
  }

  /** Checks whether the two cards are exactly the same or if two different colors were assigned Trumpf
   * 
   * @param a Card to check for eligibility
   * @throws RunTimeException */
  public void CheatChecker(JassCards a) {
    if (this.color.equals(a.color) && this.cardType.equals(a.cardType)) {
      System.err.println("There are no duplicate cards in a deck!");
      throw new RuntimeException();
    }
    if (this.isTrumpf && a.isTrumpf && !this.color.equals(a.color)) {
      System.err.println("Only card color can be assigned Trumpf!");
      throw new RuntimeException();
    }
  }
}
