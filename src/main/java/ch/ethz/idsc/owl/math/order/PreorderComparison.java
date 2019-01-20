// code by astoll
package ch.ethz.idsc.owl.math.order;

/** Creates an enumeration for a preorder.
 * 
 * <p> A preorder (or quasi-order) is a binary relation between two elements in a set.
 * <p> By definition, a preorder is transitive and reflexive.
 * <p> In a partial order ({@link PartialComparator}) <tt>a &le b</tt> and <tt>a &ge b</tt> means that <tt>a = b</tt>,
 * whereas in preorder this not the case (e.g. there can be cycles)
 * <table>
 * <tr>
 * <th>Relation</th>
 * <th>Instance</th>
 * </tr>
 * <tr>
 * <td>A &le; B</td>
 * <td>LESS_EQUALS</td>
 * </tr>
 * <tr>
 * <td>A &ge; B</td>
 * <td>GRETAER_EQUALS</td>
 * </tr>
 * <tr>
 * <td>A & B are incomparable </td>
 * <td>INCOMPARABLE</td>
 * </tr>
 * </table>
 * @author astoll */
public enum PreorderComparison {
  LESS_EQUALS, //
  GREATER_EQUALS, //
  INCOMPARABLE, //
  ;
}
