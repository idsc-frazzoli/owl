// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.util.List;

public class UniversalLexicographicOrder<T> implements UniversalComparator<List<T>> {
  public UniversalLexicographicOrder(List<UniversalComparator<T>> comparatorList) {
    this.comparatorList = comparatorList;
  }

  private final List<UniversalComparator<T>> comparatorList;
  // FIXME ASTOLL needs to work for tensor as well i.e. any type of iterable

  @Override // from UniversalComparator
  public UniversalComparison compare(List<T> x, List<T> y) {
    if (x.size() != y.size())
      throw new RuntimeException("Elements to compare not of same size");
    UniversalComparison LexicoComparison = UniversalComparison.INDIFFERENT;
    for (int index = 0; index < x.size(); ++index) {
      UniversalComparison stepComparison = comparatorList.get(index).compare(x.get(index), y.get(index));
      if (stepComparison.equals(UniversalComparison.STRICTLY_PRECEDES) || stepComparison.equals(UniversalComparison.STRICTLY_SUCCEDES)) {
        return stepComparison;
      } else if (stepComparison.equals(UniversalComparison.INCOMPARABLE)) {
        LexicoComparison = stepComparison;
      }
    }
    return LexicoComparison;
  }
  // public boolean qualifiedForComp(List<T> x, List<T> y) {
  // boolean notSameSize = (x.size() != y.size());
  // boolean notSameType = x.stream().anyMatch(index -> x.get((int) index).getClass().isInstance(y.get((int) index)));
  // return notSameSize;
  // // FIXME ASTOLL
  // }
}
