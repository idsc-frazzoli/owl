// code by jph
package ch.ethz.idsc.owl.math.order;

import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.sca.Clip;

/**"Interval order": left—to—right precedence relation
 * <p>One interval I1, being considered less than another, I2, 
 * if I1 is completely to the left of I2
 * <p>For two intervals <tt>x_i = [l_i,r_i] </tt> and <tt>x_j = [l_j, r_j]</tt>, <tt>x_i R x_j</tt> is satisfied if <tt>r_i &lt l_j </tt>
 * https://en.wikipedia.org/wiki/Interval_order */
public enum ClipPartialComparator {
  ;
  /** reflexive
   * antisymmetric */
  public static final PartialComparator<Clip> INSTANCE = PartialOrder.comparator((x, y) -> Scalars.lessEquals(x.max(), y.min()));
}
