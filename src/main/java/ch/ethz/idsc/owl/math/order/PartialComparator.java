// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.util.Comparator;

/** the name of the interface is inspired by the java interface
 * for the comparison in a total order {@link Comparator} */
@FunctionalInterface
public interface PartialComparator<T> {
  /** Compares to elements of a proset.
   * 
   * For two elements a and b in a proset exactly one of the following holds:
   * <p>
   * <tt>a = b</tt>,
   * <tt>a < b</tt>,
   * <tt>a > b</tt>, or
   * <tt>a ~ b</tt>, i.e. a and b are incomparable
   * @param a left hand side of partial comparison
   * @param b right hand side of partial comparison
   * @return one of the following
   * PartialComparison.EQUALS if a = b,
   * PartialComparison.LESS_THAN if a < b,
   * PartialComparison.GREATER_THAN if a > b or
   * PartialComparison.INCOMPARABLE if a and b are incomparable */
  PartialComparison compare(T a, T b);
}
