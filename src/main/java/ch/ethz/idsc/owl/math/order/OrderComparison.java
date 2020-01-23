// code by astoll
package ch.ethz.idsc.owl.math.order;

/** Creates an enumeration for a comparison of paired objects.
 * 
 * <p>An ordered pair is pair of objects <tt>(x, y)</tt> associated with an order (binary) relation <tt>R</tt>.
 * 
 * <p>If the ordered pair is in the relation <tt>R</tt>, we write <tt>xRy</tt>.
 * 
 * <p>Given two elements <tt>x</tt> and <tt>y</tt> and an order relation <tt>R</tt> we can define four cases:
 * 
 * <table>
 * <tr>
 * <th>Relation</th>
 * <th>Instance</th>
 * </tr>
 * <tr>
 * <td>xRy and not yRx</td>
 * <td>STRICTLY_PRECEDES</td>
 * </tr>
 * <tr>
 * <td>yRx and not xRy</td>
 * <td>STRICTLY_SUCCEDES</td>
 * </tr>
 * <tr>
 * <td>xRy and yRx</td>
 * <td>INDIFFERENT</td>
 * </tr>
 * <tr>
 * <td>neither xRy nor yRx</td>
 * <td>INCOMPARABLE</td>
 * </tr>
 * </table>
 * 
 * See Chapter 2.2 in "Multi-Objective Optimization Using Preference Structures" */
public enum OrderComparison {
  STRICTLY_PRECEDES, //
  INDIFFERENT, //
  STRICTLY_SUCCEEDS, //
  INCOMPARABLE, //
  ;
}
