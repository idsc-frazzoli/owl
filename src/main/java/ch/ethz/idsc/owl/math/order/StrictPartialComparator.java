// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.util.Comparator;

/** the name of the interface is inspired by the java interface
 * for the comparison in a total order {@link Comparator} */
@FunctionalInterface
public interface StrictPartialComparator<T> {
  /** Compares to elements of a STRICT poset.
   * 
   * For two elements a and b in a poset exactly one of the following holds:
   * <p>
   * <tt>a &lt b</tt>,
   * <tt>a > b</tt>, or
   * <tt>a ~ b</tt>, i.e. a and b are incomparable
   * @param a left hand side of partial comparison
   * @param b right hand side of partial comparison
   * @return one of the following
   * PartialComparison.LESS_THAN if a < b,
   * PartialComparison.GREATER_THAN if a > b or
   * PartialComparison.INCOMPARABLE if a and b are incomparable */
  StrictPartialComparison compare(T a, T b);
}
