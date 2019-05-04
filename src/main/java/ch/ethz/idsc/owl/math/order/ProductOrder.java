// code by astoll
package ch.ethz.idsc.owl.math.order;

public enum ProductOrder {
  ;
  /** @param c1
   * @param c2
   * @return */
  public static OrderComparison intersect(OrderComparison c1, OrderComparison c2) {
    if (c1.equals(OrderComparison.INDIFFERENT))
      return c2;
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
