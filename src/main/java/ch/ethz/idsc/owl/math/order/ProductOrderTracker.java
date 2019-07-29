// code by jph
package ch.ethz.idsc.owl.math.order;

/** digests pairs to compare
 * 
 * ProductOrderTracker uses one comparator for each comparison
 * 
 * for a more general product order comparator
 * @see ProductOrderComparator
 * See Chapter 2.7.4 in "Multi-Objective Optimization Using Preference Structures" */
public class ProductOrderTracker<T> {
  private final OrderComparator<T> orderComparator;
  private OrderComparison orderComparison = OrderComparison.INDIFFERENT;

  public ProductOrderTracker(OrderComparator<T> orderComparator) {
    this.orderComparator = orderComparator;
  }

  /** @param x
   * @param y
   * @return result of product order comparison of all previous input pairs and given pair (x, y) */
  public OrderComparison digest(T x, T y) {
    return orderComparison = ProductOrder.intersect(orderComparison, orderComparator.compare(x, y));
  }
}
