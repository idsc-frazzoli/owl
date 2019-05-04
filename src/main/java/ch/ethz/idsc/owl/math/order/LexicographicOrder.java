// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.util.List;

public class LexicographicOrder<T> implements OrderComparator<List<T>> {
  public LexicographicOrder(List<OrderComparator<T>> comparatorList) {
    this.comparatorList = comparatorList;
  }

  private final List<OrderComparator<T>> comparatorList;
  // FIXME ASTOLL needs to work for tensor as well i.e. any type of iterable

  @Override // from UniversalComparator
  public OrderComparison compare(List<T> x, List<T> y) {
    if (x.size() != y.size())
      throw new RuntimeException("Elements to compare not of same size");
    OrderComparison orderComparison = OrderComparison.INDIFFERENT;
    for (int index = 0; index < x.size(); ++index) {
      OrderComparison stepComparison = comparatorList.get(index).compare(x.get(index), y.get(index));
      if (stepComparison.equals(OrderComparison.STRICTLY_PRECEDES) || //
          stepComparison.equals(OrderComparison.STRICTLY_SUCCEEDS))
        return stepComparison;
      else //
      if (stepComparison.equals(OrderComparison.INCOMPARABLE))
        orderComparison = stepComparison;
    }
    return orderComparison;
  }
  // public boolean qualifiedForComp(List<T> x, List<T> y) {
  // boolean notSameSize = (x.size() != y.size());
  // boolean notSameType = x.stream().anyMatch(index -> x.get((int) index).getClass().isInstance(y.get((int) index)));
  // return notSameSize;
  // // FIXME ASTOLL
  // }
}
