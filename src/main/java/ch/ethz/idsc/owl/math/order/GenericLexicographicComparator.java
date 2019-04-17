// code by astoll, jph
package ch.ethz.idsc.owl.math.order;

import java.util.Iterator;
import java.util.List;

/** Creates a lexicographic order where two tuples are compared to each other. An element x precedes another element
 * y if there is an index where x_i precedes y_i and for all indices j smaller than i x_j is indifferent y_j. */
public class GenericLexicographicComparator implements OrderComparator<Iterable<? extends Object>> {
  private final List<OrderComparator> comparatorList;

  public GenericLexicographicComparator(List<OrderComparator> comparatorList) {
    this.comparatorList = comparatorList;
  }

  @Override
  public OrderComparison compare(Iterable<? extends Object> x, Iterable<? extends Object> y) {
    OrderComparison orderComparison = OrderComparison.INDIFFERENT;
    Iterator<? extends Object> x_iterator = x.iterator();
    Iterator<? extends Object> y_iterator = y.iterator();
    int index = 0;
    while (true) {
      if (!x_iterator.hasNext()) {
        if (y_iterator.hasNext())
          throw new RuntimeException("x and y not of same size!");
        return orderComparison;
      }
      OrderComparison comparison = comparatorList.get(index).compare(x_iterator.next(), y_iterator.next());
      if (!comparison.equals(OrderComparison.INDIFFERENT))
        return comparison;
      ++index;
    }
  }
}
