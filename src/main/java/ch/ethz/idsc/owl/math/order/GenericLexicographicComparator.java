// code by astoll, jph
package ch.ethz.idsc.owl.math.order;

import java.util.Iterator;
import java.util.List;

/** Creates a lexicographic order where two tuples are compared to each other. An element x precedes another element
 * y if there is an index where x_i precedes y_i and for all indices j smaller than i x_j is indifferent y_j. */
public class GenericLexicographicComparator implements OrderComparator<Iterable<? extends Object>> {
  private final List<OrderComparator> orderComparators;

  public GenericLexicographicComparator(List<OrderComparator> orderComparators) {
    this.orderComparators = orderComparators;
  }

  @Override // from OrderComparator
  public OrderComparison compare(Iterable<? extends Object> x, Iterable<? extends Object> y) {
    Iterator<? extends Object> x_iterator = x.iterator();
    Iterator<? extends Object> y_iterator = y.iterator();
    int index = 0;
    while (true) {
      if (!x_iterator.hasNext()) {
        if (y_iterator.hasNext())
          throw new RuntimeException("x and y not of same size!");
        return OrderComparison.INDIFFERENT;
      }
      OrderComparison orderComparison = orderComparators.get(index).compare(x_iterator.next(), y_iterator.next());
      if (!orderComparison.equals(OrderComparison.INDIFFERENT))
        return orderComparison;
      ++index;
    }
  }
}
