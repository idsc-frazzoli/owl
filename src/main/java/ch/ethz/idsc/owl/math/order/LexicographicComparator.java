// code by astoll, jph
package ch.ethz.idsc.owl.math.order;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

/** Creates a lexicographic order where two tuples are compared to each other.
 * An element x precedes another element y if there is an index where x_i
 * precedes y_i and for all indices j smaller than i x_j is indifferent y_j.
 * See Chapter 2.7.5 in "Multi-Objective Optimization Using Preference Structures" */
public class LexicographicComparator implements OrderComparator<Iterable<? extends Object>>, Serializable {
  private final List<OrderComparator<? extends Object>> orderComparators;

  public LexicographicComparator(List<OrderComparator<? extends Object>> orderComparators) {
    this.orderComparators = orderComparators;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override // from OrderComparator
  public OrderComparison compare(Iterable<? extends Object> x, Iterable<? extends Object> y) {
    Iterator<? extends Object> x_iterator = x.iterator();
    Iterator<? extends Object> y_iterator = y.iterator();
    OrderComparison orderComparison = OrderComparison.INDIFFERENT;
    for (OrderComparator orderComparator : orderComparators) {
      orderComparison = orderComparator.compare(x_iterator.next(), y_iterator.next());
      if (!orderComparison.equals(OrderComparison.INDIFFERENT))
        break;
    }
    return orderComparison;
  }
}
