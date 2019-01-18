// code by astoll
package ch.ethz.idsc.owl.math.order;

/** Creates an enumeration for a partial ordering.
 * 
 * <p> A partial order is a binary relation between two elements in a set.
 * <p> By definition, a partial order is transitive, irreflexive and asymmetric.
 * 
 * <p> In contrast to a (non-strict) partial order ({@link PartialComparision} ), an element does not relate to its self.
 * 
 * <table>
 * <tr>
 * <th>Relation</th>
 * <th>Instance</th>
 * </tr>
 * <tr>
 * <td>A &le; B</td>
 * <td>LESS_THAN</td>
 * </tr>
 * <tr>
 * <td>A = B</td>
 * <td>EQUALS</td>
 * </tr>
 * <tr>
 * <td>A &ge; B</td>
 * <td>GRETAER_THAN</td>
 * </tr>
 * <tr>
 * <td>A & B are incomparable </td>
 * <td>INCOMPARABLE</td>
 * </tr>
 * </table>
 * @author astoll */
public enum StrictPartialComparison {
  LESS_THAN, //
  GREATER_THAN, //
  INCOMPARABLE, //
  ;
}
