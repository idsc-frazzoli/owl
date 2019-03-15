// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/** Creates a product order comparator where each elements of two tuples are compared coordinatewise.
 * 
 * @author Andre
 *
 * @param <T> */
public class UniversalProductOrder<T> implements UniversalComparator<List<T>> {
  public UniversalProductOrder(List<UniversalComparator<T>> comparatorList) {
    this.comparatorList = comparatorList;
  }

  private final List<UniversalComparator<T>> comparatorList;

  /** <p> Two tuples x and y are compared componentwise.
   * <p> xRy if for all i: x_i R_i y_i . */
  @Override // from UniversalComparator
  public UniversalComparison compare(List<T> x, List<T> y) {
    if (x.size() != y.size())
      throw new RuntimeException("Elements to compare not of same size");
    Set<UniversalComparison> comparisonSet = IntStream.range(0, x.size()) //
        .mapToObj(index -> comparatorList.get(index).compare(x.get(index), y.get(index))) //
        .collect(Collectors.toSet());
    if (elementsIncomparable(comparisonSet))
      return UniversalComparison.INCOMPARABLE;
    else if (comparisonSet.contains(UniversalComparison.STRICTLY_PRECEDES))
      return UniversalComparison.STRICTLY_PRECEDES;
    else if (comparisonSet.contains(UniversalComparison.STRICTLY_SUCCEDES))
      return UniversalComparison.STRICTLY_SUCCEDES;
    else
      return UniversalComparison.INDIFFERENT;
  }

  /** The product order is incomparable if any two elements of x and y are incomparable or
   * if strict precedence and strict succession are present
   * 
   * @param comparisonSet
   * @return true if product order is incomparable, false otherwise */
  public boolean elementsIncomparable(Set<UniversalComparison> comparisonSet) {
    return comparisonSet.contains(UniversalComparison.INCOMPARABLE) //
        || (comparisonSet.contains(UniversalComparison.STRICTLY_SUCCEDES) && comparisonSet.contains(UniversalComparison.STRICTLY_PRECEDES));
  }
}
