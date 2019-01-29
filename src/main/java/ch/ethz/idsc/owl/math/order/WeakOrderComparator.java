// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.util.Comparator;

/** the name of the interface is inspired by the java interface
 * for the comparison in a total order {@link Comparator}
 * 
 * https://en.wikipedia.org/wiki/Preorder */
@FunctionalInterface
public interface WeakOrderComparator<T> {
  /** Compares to elements in a weakly ordered set.
   * 
   * For two elements x and y in a weakly ordered set one of the following holds:
   * <p>
   * <tt>x &le y</tt>,
   * <tt>x &ge y</tt>, or
   * <tt>x ~ y</tt>, i.e. x and y are indifferent
   * @param x left hand side of weakly ordered comparison
   * @param y right hand side of weakly ordered comparison
   * @return one of the following
   * WeakOrderComparison.LESS_EQUALS_ONLY if <tt>x &le y</tt>,
   * WeakOrder.GREATER_EQUALS_ONLY if <tt>x &ge y</tt>, or
   * WeakOrder.INDIFFERENT if x and y are indifferent (x &le y and x &ge y) */
  WeakOrderComparison compare(T x, T y);
}
