// code by astoll
package ch.ethz.idsc.owl.math.order;

/** Creates an enumeration for a comparison of paired objects.
 * 
 * <p>An ordered pair is pair of objects <tt>(x,y)</tt> assocaited with an order (binary) relation <tt>R</tt>.
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
 * <td>xRy and not yRX</td>
 * <td>STRICTLY_PRECEDES</td>
 * </tr>
 * <tr>
 * <td>yRX and not xRy</td>
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
 * @author astoll */
public enum UniversalComparison {
  STRICTLY_PRECEDES(true, false), //
  INDIFFERENT(true, true), //
  STRICTLY_SUCCEDES(false, true), //
  INCOMPARABLE(false, false), //
  ;
  private final boolean le;
  private final boolean ge;

  private UniversalComparison(boolean le, boolean ge) {
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
