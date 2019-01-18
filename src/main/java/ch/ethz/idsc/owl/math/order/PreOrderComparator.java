package ch.ethz.idsc.owl.math.order;

import java.util.Comparator;

/** the name of the interface is inspired by the java interface
 * for the comparison in a total order {@link Comparator} */
@FunctionalInterface
public interface PreOrderComparator<T> {
  /** Compares to elements in a preordered set.
   * 
   * For two elements a and b in a preordered set one of the following holds:
   * <p>
   * <tt>a &le b</tt>,
   * <tt>a &ge b</tt>, or
   * <tt>a ~ b</tt>, i.e. a and b are incomparable
   * @param a left hand side of preordered comparison
   * @param b right hand side of preordered comparison
   * @return one of the following
   * PreOrderComparison.LESS_EQUALS if <tt>a &le b</tt>,
   * PreOrder.GREATER_EQUALS if <tt>a &ge b</tt> or
   * PreOrder.INCOMPARABLE if a and b are incomparable */
  PreOrderComparison compare(T a, T b);
}
