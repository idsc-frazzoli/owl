// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.util.Comparator;

/** the name of the interface is inspired by the java interface
 * for the comparison in a total order {@link Comparator} */
@FunctionalInterface
public interface OrderComparator<T> {
  /** Compares to elements of an ordered set.
   * <p> The comparison is defined such that if x precedes y then
   * <p>
   * <tt>x &lt y</tt>,
   * <tt>x > y</tt>, or
   * <tt>x ~ y</tt>, i.e. x and y are incomparable
   * @param x left hand side of comparison
   * @param y right hand side of comparison
   * @return one of the following
   * UniversalComparison.STRICTLY_PRECEDES if xRy and not yRx,
   * UniversalComparison.STRICTLY_SUCCEDES if yRx and not xRy,
   * UniversalComparison.INDIFFERENT xRy and yRx
   * UniversalComparison.INCOMPARABLE if neither xRy and nor yRx
   * 
   * See Chapter 2.2 in "Multi-Objective Optimization Using Preference Structures" */
  OrderComparison compare(T x, T y);
}
