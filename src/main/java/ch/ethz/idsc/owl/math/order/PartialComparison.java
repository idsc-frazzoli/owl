// code by astoll, jph
package ch.ethz.idsc.owl.math.order;

/** Creates an enumeration for a partial ordering.
 * 
 * <p>A partial order is a binary relation between two elements in a set.
 * <p>By definition, a partial order is reflexive, antisymmetric, and transitive.
 * 
 * <table>
 * <tr>
 * <th>Relation</th>
 * <th>Instance</th>
 * </tr>
 * <tr>
 * <td>x &le; y</td>
 * <td>LESS_THAN</td>
 * </tr>
 * <tr>
 * <td>x = y</td>
 * <td>EQUALS</td>
 * </tr>
 * <tr>
 * <td>x &ge; y</td>
 * <td>GREATER_THAN</td>
 * </tr>
 * <tr>
 * <td>x & y are incomparable </td>
 * <td>INCOMPARABLE</td>
 * </tr>
 * </table>
 * @author astoll */
public enum PartialComparison {
  LESS_THAN, //
  EQUALS, //
  GREATER_THAN, //
  INCOMPARABLE, //
  ;
}
