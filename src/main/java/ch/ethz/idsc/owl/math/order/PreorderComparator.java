// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.util.Comparator;

/** the name of the interface is inspired by the java interface
 * for the comparison in a total order {@link Comparator}
 * 
 * https://en.wikipedia.org/wiki/Preorder */
@FunctionalInterface
public interface PreorderComparator<T> {
  /** Compares to elements in a preordered set.
   * 
   * For two elements x and y in a preordered set one of the following holds:
   * <p>
   * <tt>x &le y</tt>,
   * <tt>x &ge y</tt>, or
   * <tt>x || y</tt>, i.e. x and y are incomparable
   * @param x left hand side of preordered comparison
   * @param y right hand side of preordered comparison
   * @return one of the following
   * PreorderComparison.LESS_EQUALS if <tt>x &le y</tt>,
   * Preorder.GREATER_EQUALS if <tt>x &ge y</tt>, or
   * Preorder.INCOMPARABLE if x and y are incomparable */
  PreorderComparison compare(T x, T y);
}
