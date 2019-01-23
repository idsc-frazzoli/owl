// code by astoll
package ch.ethz.idsc.owl.math.order;

/** Creates an enumeration for a partial ordering.
 * 
 * <p>A partial order is a binary relation between two elements in a set.
 * <p>By definition, a partial order is irreflexive, asymmetric, and transitive.
 * 
 * <p>In contrast to a (non-strict) partial order ({@link PartialComparision}),
 * an element does not relate to its self.
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
 * <td>x &ge; y</td>
 * <td>GRETAER_THAN</td>
 * </tr>
 * <tr>
 * <td>x & y are incomparable </td>
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
