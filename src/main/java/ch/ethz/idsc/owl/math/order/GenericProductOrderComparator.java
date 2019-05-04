// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.util.Iterator;
import java.util.List;

/** Creates a product order comparator where each elements of two tuples are compared coordinatewise.
 * An element x precedes y if it precedes y in all coordinates. */
public class GenericProductOrderComparator implements OrderComparator<Iterable<? extends Object>> {
  private final List<OrderComparator> orderComparators;

  public GenericProductOrderComparator(List<OrderComparator> orderComparators) {
    this.orderComparators = orderComparators;
  }

  @Override // from OrderComparator
  public OrderComparison compare(Iterable<? extends Object> x, Iterable<? extends Object> y) {
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
