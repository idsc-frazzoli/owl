// code by astoll
package ch.ethz.idsc.owl.math.order;

/** Creates an enumeration for a preorder.
 * 
 * <p>A preorder (or quasi-order) is a binary relation between two elements in a set.
 * 
 * <p>By definition, a preorder is transitive and reflexive.
 * 
 * <p>In a partial order ({@link PartialComparator}) <tt>x &le y</tt> and <tt>x &ge y</tt> means that <tt>x = y</tt>,
 * whereas in preorder this not the case (e.g. there can be cycles)
 * 
 * <table>
 * <tr>
 * <th>Relation</th>
 * <th>Instance</th>
 * </tr>
 * <tr>
 * <td>x &le; y</td>
 * <td>LESS_EQUALS</td>
 * </tr>
 * <tr>
 * <td>x &ge; y</td>
 * <td>GREATER_EQUALS</td>
 * </tr>
 * <tr>
 * <td>x & y are incomparable </td>
 * <td>INCOMPARABLE</td>
 * </tr>
 * </table>
 * @author astoll */
public enum PreorderComparison {
  LESS_EQUALS_ONLY(true, false), //
  LESS_EQUALS_AND_GREATER_EQUALS(true, true), //
  GREATER_EQUALS_ONLY(false, true), //
  INCOMPARABLE(false, false), //
  ;
  private final boolean le;
  private final boolean ge;

  private PreorderComparison(boolean le, boolean ge) {
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
