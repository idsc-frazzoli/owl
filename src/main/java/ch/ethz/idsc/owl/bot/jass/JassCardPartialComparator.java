// code by astoll
package ch.ethz.idsc.owl.bot.jass;

import ch.ethz.idsc.owl.math.order.StrictPartialComparator;
import ch.ethz.idsc.owl.math.order.StrictPartialOrder;

/** Creates a STRICT partial order for cards of deck according to the rules of "Jassen"
 * 
 * @author astoll */
public enum JassCardPartialComparator {
  ;
  /** Compares cards according to their value in the traditional Swiss card game of Jassen
   * 
   * <p>The deck consists four different card colors: Eicheln, Rosen, Schilten and Schellen
   * <p>Each color contains nine cards: 6, 7, 8, 9, 10, Jack, Queen, King and Ace (listed with increasing value).
   * <p>At the beginning of the game one color is assigned to be "Trumpf" meaning it tops all other cards of different colors.
   * Within one color a card with a higher value is better than a card with lower value and
   * cards of different colors (none of which is assigned "Trumpf") are incomparable.
   * <p>Since there are no duplicate cards in a deck the order is a strict partial order. (A = B cannot be true)
   * <p>
   * <table>
   * <tr>
   * <th>Relation</th>
   * <th>Explanation</th>
   * </tr>
   * <tr>
   * <td>a = b</td>
   * <td>NA since there are no duplicate cards</td>
   * </tr>
   * <tr>
   * <td>a &lt; b</td>
   * <td>Card A is better than card B</td>
   * </tr>
   * <tr>
   * <td>a &gt; b</td>
   * <td>Card B is better than Card A</td>
   * </tr>
   * <tr>
   * <td>Incomparable </td>
   * <td>Different colors and neither is "Trumpf"</td>
   * </tr>
   * </table> */
  public static final StrictPartialComparator<JassCard> INSTANCE = //
      StrictPartialOrder.comparator((a, b) -> b.isGreater(a));
}
