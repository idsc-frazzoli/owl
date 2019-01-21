// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.util.Collection;

/** Creates partial ordering of sets.
 * 
 * <p>For two sets x and y, x < y is satisfied if x is a subset of y.
 * 
 * @author astoll */
public enum SetPartialComparator {
  ;
  /** Compares to sets according to their elements
   * 
   * <table>
   * <tr>
   * <th>Relation</th>
   * <th>Explanation</th>
   * </tr>
   * <tr>
   * <td>x = y</td>
   * <td>All elements in x are contained in y and vice versa</td>
   * </tr>
   * <tr>
   * <td>x &le; b</td>
   * <td>x is a subset of y</td>
   * </tr>
   * <tr>
   * <td>x &ge; y</td>
   * <td>y is a subset of x</td>
   * </tr>
   * <tr>
   * <td>Incomparable </td>
   * <td>Neither set is a subset of the other</td>
   * </tr>
   * </table> */
  public static final PartialComparator<Collection<?>> INSTANCE = //
      PartialOrder.comparator((x, y) -> y.containsAll(x));
}
