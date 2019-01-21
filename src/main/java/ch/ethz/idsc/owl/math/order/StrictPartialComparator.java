// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.util.Comparator;

/** the name of the interface is inspired by the java interface
 * for the comparison in a total order {@link Comparator} */
@FunctionalInterface
public interface StrictPartialComparator<T> {
  /** Compares to elements of a STRICT poset.
   * 
   * For two elements x and y in a poset exactly one of the following holds:
   * <p>
   * <tt>x &lt y</tt>,
   * <tt>x > y</tt>, or
   * <tt>x ~ y</tt>, i.e. x and y are incomparable
   * @param x left hand side of partial comparison
   * @param y right hand side of partial comparison
   * @return one of the following
   * PartialComparison.LESS_THAN if x < y,
   * PartialComparison.GREATER_THAN if x > y or
   * PartialComparison.INCOMPARABLE if x and y are incomparable */
  StrictPartialComparison compare(T x, T y);
}
