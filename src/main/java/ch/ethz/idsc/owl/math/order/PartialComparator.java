// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.util.Comparator;

/** the name of the interface is inspired by the java interface
 * for the comparison in a total order {@link Comparator} */
@FunctionalInterface
public interface PartialComparator<T> {
  /** Compares to elements of a poset.
   * 
   * For two elements x and y in a poset exactly one of the following holds:
   * <p>
   * <tt>x = y</tt>,
   * <tt>x < y</tt>,
   * <tt>x > y</tt>, or
   * <tt>x ~ y</tt>, i.e. x and y are incomparable
   * @param x left hand side of partial comparison
   * @param y right hand side of partial comparison
   * @return one of the following
   * PartialComparison.EQUALS if x = y,
   * PartialComparison.LESS_THAN if x < y,
   * PartialComparison.GREATER_THAN if x > y or
   * PartialComparison.INCOMPARABLE if x and y are incomparable */
  PartialComparison compare(T x, T y);
}
