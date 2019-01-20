// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.util.Collection;

/** Creates partial ordering of sets.
 * 
 * <p>For two sets A and B, A < B is satisfied if A is a subset of B.
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
   * <td>A = B</td>
   * <td>All elements in A are contained in B and vice versa</td>
   * </tr>
   * <tr>
   * <td>A &le; B</td>
   * <td>A is a subset of B</td>
   * </tr>
   * <tr>
   * <td>A &ge; B</td>
   * <td>B is a subset of A</td>
   * </tr>
   * <tr>
   * <td>Incomparable </td>
   * <td>Neither set is a subset of the other</td>
   * </tr>
   * </table> */
  public static final PartialComparator<Collection<?>> INSTANCE = //
      PartialOrder.comparator((a, b) -> b.containsAll(a));
}
