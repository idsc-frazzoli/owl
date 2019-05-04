// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.util.Iterator;
import java.util.List;

public class GenericProductOrderComparator implements OrderComparator<Iterable<? extends Object>> {
  private final List<OrderComparator> orderComparators;

  public GenericProductOrderComparator(List<OrderComparator> orderComparators) {
    this.orderComparators = orderComparators;
  }

  @Override // from OrderComparator
  public OrderComparison compare(Iterable<? extends Object> x, Iterable<? extends Object> y) {
    Iterator<? extends Object> x_iterator = x.iterator();
    Iterator<? extends Object> y_iterator = y.iterator();
    int index = 0;
    OrderComparison orderComparison = orderComparators.get(index).compare(x_iterator.next(), y_iterator.next());
    while (true) {
      if (!x_iterator.hasNext()) {
        if (y_iterator.hasNext())
          throw new RuntimeException("Objects not of same size!!");
        return orderComparison;
      }
      ++index;
      if (orderComparators.size() - 1 < index) {
        throw new RuntimeException("ComparatorList not same size as objects to compare!");
      }
      OrderComparison nextComparison = orderComparators.get(index).compare(x_iterator.next(), y_iterator.next());
      orderComparison = updateOrderComparison(orderComparison, nextComparison);
      if (orderComparison.equals(OrderComparison.INCOMPARABLE))
        return orderComparison;
    }
  }

  private static OrderComparison updateOrderComparison(OrderComparison c1, OrderComparison c2) {
    if (c1.equals(OrderComparison.INDIFFERENT))
      return c2;
    if (c2.equals(OrderComparison.INDIFFERENT))
      return c1;
    // ---
    if (c1.equals(OrderComparison.INCOMPARABLE) || //
        c2.equals(OrderComparison.INCOMPARABLE))
      return OrderComparison.INCOMPARABLE;
    // ---
    if (!c1.equals(c2))
      return OrderComparison.INCOMPARABLE;
    return c1;
  }
}
