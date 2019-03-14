// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.util.List;

public class LexicographicSemiorderComparator<T> implements StrictPartialComparator<List<T>> {
  public LexicographicSemiorderComparator(List<StrictPartialComparator<T>> strictPartialComparatorList) {
    this.strictPartialComparatorList = strictPartialComparatorList;
  }

  List<StrictPartialComparator<T>> strictPartialComparatorList;

  @Override
  public StrictPartialComparison compare(List<T> x, List<T> y) {
    if (x.size() != y.size())
      throw new RuntimeException("Elements to compare not of same size");
    for (int index = 0; index < x.size(); ++index) {
      StrictPartialComparison strictPartialComparison = strictPartialComparatorList.get(index).compare(x.get(index), y.get(index));
      if (strictPartialComparison.equals(StrictPartialComparison.LESS_THAN)) {
        return StrictPartialComparison.LESS_THAN;
      } else if (strictPartialComparison.equals(StrictPartialComparison.GREATER_THAN)) {
        return StrictPartialComparison.GREATER_THAN;
      }
    }
    return StrictPartialComparison.INCOMPARABLE;
  }
}
