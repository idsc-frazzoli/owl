// code by astoll, jph
package ch.ethz.idsc.owl.math.order;

import java.util.Iterator;
import java.util.List;

// TODO ASTOLL definition is missing
public class GenericLexicographicComparator implements UniversalComparator<Iterable<? extends Object>> {
  private final List<UniversalComparator> comparatorList;

  public GenericLexicographicComparator(List<UniversalComparator> comparatorList) {
    this.comparatorList = comparatorList;
  }

  @Override
  public UniversalComparison compare(Iterable<? extends Object> x, Iterable<? extends Object> y) {
    UniversalComparison universalComparison = UniversalComparison.INDIFFERENT;
    Iterator<? extends Object> x_iterator = x.iterator();
    Iterator<? extends Object> y_iterator = y.iterator();
    int index = 0;
    while (true) {
      if (!x_iterator.hasNext()) {
        if (y_iterator.hasNext())
          throw new RuntimeException("some");
        return universalComparison;
      }
      UniversalComparison comparison = comparatorList.get(index).compare(x_iterator.next(), y_iterator.next());
      if (comparison.equals(UniversalComparison.STRICTLY_PRECEDES) || //
          comparison.equals(UniversalComparison.STRICTLY_SUCCEDES)) {
        return comparison;
      } else //
      if (comparison.equals(UniversalComparison.INCOMPARABLE)) {
        // TODO ASTOLL should this case return: INCOMPARABLE ?
        universalComparison = comparison;
      }
      ++index;
    }
  }
}
