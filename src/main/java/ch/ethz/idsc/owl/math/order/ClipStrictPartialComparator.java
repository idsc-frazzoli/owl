// code by jph and astoll
package ch.ethz.idsc.owl.math.order;

import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.sca.Clip;

/** Implements an interval order for intervals on the real numbers.
 * <p>An interval order is a special kind of strict partial order, thus, it is transitive, irreflexive and asymmetric.
 * <p>One interval I1, being considered less than another, I2,
 * if I1 is completely to the left of I2
 * <p>For two intervals <tt>x_i = [l_i,r_i] </tt> and <tt>x_j = [l_j, r_j]</tt>, <tt>x_i R x_j</tt> is satisfied if and only if <tt>r_i &lt l_j </tt>. In other
 * words, interval <tt> x_i</tt> has to end before <tt> x_j </tt>.
 * https://en.wikipedia.org/wiki/Interval_order */
public enum ClipStrictPartialComparator {
  ;
  /** binary relation
   * irreflexive */
  public static final BinaryRelation<Clip> BINARY_RELATION = (x, y) -> Scalars.lessThan(x.max(), y.min());
  public static final StrictPartialComparator<Clip> INSTANCE = StrictPartialOrder.comparator(BINARY_RELATION);
}
