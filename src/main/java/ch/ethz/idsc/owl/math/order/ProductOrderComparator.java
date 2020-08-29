// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

/** Creates a product order comparator where each elements of two tuples are compared coordinatewise.
 * An element x precedes y if it precedes y in all coordinates.
 * See Chapter 2.7.4 in "Multi-Objective Optimization Using Preference Structures" */
public class ProductOrderComparator implements OrderComparator<Iterable<? extends Object>>, Serializable {
  private final List<OrderComparator<? extends Object>> orderComparators;

  /** @param orderComparators */
  public ProductOrderComparator(List<OrderComparator<? extends Object>> orderComparators) {
    this.orderComparators = orderComparators;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override // from OrderComparator
  public final OrderComparison compare(Iterable<? extends Object> x, Iterable<? extends Object> y) {
    Iterator<? extends Object> x_iterator = x.iterator();
    Iterator<? extends Object> y_iterator = y.iterator();
    OrderComparison orderComparison = OrderComparison.INDIFFERENT;
    for (OrderComparator orderComparator : orderComparators) {
      orderComparison = ProductOrder.intersect(orderComparison, orderComparator.compare(x_iterator.next(), y_iterator.next()));
      if (orderComparison.equals(OrderComparison.INCOMPARABLE))
        break;
    }
    return orderComparison;
  }
}
