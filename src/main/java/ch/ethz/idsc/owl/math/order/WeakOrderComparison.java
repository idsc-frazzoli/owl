// code by astoll
package ch.ethz.idsc.owl.math.order;

/** Creates an enumeration for a weak order.
 * 
 * <p>A weak order is a binary relation between two elements in a set.
 * 
 * <p>By definition, a weak order is transitive reflexive and connected (xRy, yRx or both).
 * 
 * <table>
 * <tr>
 * <th>Relation</th>
 * <th>Instance</th>
 * </tr>
 * <tr>
 * <td>x &le; y</td>
 * <td>LESS_EQUALS_ONLY</td>
 * </tr>
 * <tr>
 * <td>x &ge; y</td>
 * <td>GREATER_EQUALS_ONLY</td>
 * </tr>
 * <tr>
 * <td>x ~ y</td>
 * <td>INDIFFERENT</td>
 * </tr>
 * </table>
 * @author astoll */
public enum WeakOrderComparison {
  LESS_EQUALS_ONLY(true, false), //
  INDIFFERENT(true, true), //
  GREATER_EQUALS_ONLY(false, true),//
  ;
  private final boolean le;
  private final boolean ge;

  private WeakOrderComparison(boolean le, boolean ge) {
    this.le = le;
    this.ge = ge;
  }

  public boolean isLessEquals() {
    return le;
  }

  public boolean isGreaterEquals() {
    return ge;
  }
}
