// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.util.Iterator;
import java.util.List;

public class GenericProductOrderComparator implements OrderComparator<Iterable<? extends Object>> {
  private final List<OrderComparator> comparatorList;

  public GenericProductOrderComparator(List<OrderComparator> comparatorList) {
    this.comparatorList = comparatorList;
  }

  @Override
  public OrderComparison compare(Iterable<? extends Object> x, Iterable<? extends Object> y) {
    Iterator<? extends Object> x_iterator = x.iterator();
    Iterator<? extends Object> y_iterator = y.iterator();
    int index = 0;
    OrderComparison comparison = comparatorList.get(index).compare(x_iterator.next(), y_iterator.next());
    while (true) {
      if (!x_iterator.hasNext()) {
        if (y_iterator.hasNext())
          throw new RuntimeException("Objects not of same size!!");
        return comparison;
      }
      ++index;
      if (comparatorList.size() - 1 < index) {
        throw new RuntimeException("ComparatorList not same size as objects to compare!");
      }
      OrderComparison nextComparison = comparatorList.get(index).compare(x_iterator.next(), y_iterator.next());
      comparison = updateOrderComparison(comparison, nextComparison);
      if (comparison.equals(OrderComparison.INCOMPARABLE))
        return comparison;
    }
  }

  private OrderComparison updateOrderComparison(OrderComparison c1, OrderComparison c2) {
    if (c1.equals(OrderComparison.INDIFFERENT))
      return c2;
    if (c2.equals(OrderComparison.INDIFFERENT))
      return c1;
    if (c1.equals(OrderComparison.INCOMPARABLE) || c2.equals(OrderComparison.INCOMPARABLE))
      return OrderComparison.INCOMPARABLE;
    if (!c1.equals(c2))
      return OrderComparison.INCOMPARABLE;
    else
      return c1;
  }
}
