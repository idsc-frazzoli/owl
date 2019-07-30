// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.util.Objects;

/** symmetric in the arguments intersect(a, b) == intersect(b, a)
 * See Chapter 2.7.4 in "Multi-Objective Optimization Using Preference Structures" */
public enum ProductOrder {
  ;
  /** @param c1
   * @param c2
   * @return */
  public static OrderComparison intersect(OrderComparison c1, OrderComparison c2) {
    if (c1.equals(OrderComparison.INDIFFERENT))
      return Objects.requireNonNull(c2);
    if (c2.equals(OrderComparison.INDIFFERENT))
      return c1;
    // ---
    if (c1.equals(OrderComparison.INCOMPARABLE) || //
        c2.equals(OrderComparison.INCOMPARABLE))
      return OrderComparison.INCOMPARABLE;
    // ---
    return c1.equals(c2) //
        ? c1
        : OrderComparison.INCOMPARABLE;
  }
}
