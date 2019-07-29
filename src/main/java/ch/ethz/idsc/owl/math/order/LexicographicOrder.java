// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.io.Serializable;
import java.util.List;

/*
 * See Chapter 2.7.5 in "Multi-Objective Optimization Using Preference Structures"
 */
public class LexicographicOrder<T> implements OrderComparator<List<T>>, Serializable {
  private final List<OrderComparator<T>> comparatorList;

  public LexicographicOrder(List<OrderComparator<T>> comparatorList) {
    this.comparatorList = comparatorList;
  }

  @Override // from UniversalComparator
  public OrderComparison compare(List<T> x, List<T> y) {
    if (x.size() != y.size())
      throw new RuntimeException("Elements of size " + x.size() + "!=" + y.size());
    OrderComparison orderComparison = OrderComparison.INDIFFERENT;
    for (int index = 0; index < x.size(); ++index) {
      OrderComparison stepComparison = comparatorList.get(index).compare(x.get(index), y.get(index));
      if (stepComparison.equals(OrderComparison.STRICTLY_PRECEDES) || //
          stepComparison.equals(OrderComparison.STRICTLY_SUCCEEDS))
        return stepComparison;
      if (stepComparison.equals(OrderComparison.INCOMPARABLE))
        orderComparison = OrderComparison.INCOMPARABLE;
    }
    return orderComparison;
  }
}
