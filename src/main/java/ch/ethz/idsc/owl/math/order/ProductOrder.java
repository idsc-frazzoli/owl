// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/** Creates a product order comparator where each elements of two tuples are compared coordinatewise.
 * An element x precedes y if it precedes y in all coordinates.
 * @author Andre
 *
 * @param <T> */
public class ProductOrder<T> implements OrderComparator<List<T>> {
  public ProductOrder(List<OrderComparator<T>> comparatorList) {
    this.comparatorList = comparatorList;
  }

  private final List<OrderComparator<T>> comparatorList;

  /** <p> Two tuples x and y are compared componentwise.
   * <p> xRy if for all i: x_i R_i y_i . */
  @Override // from UniversalComparator
  public OrderComparison compare(List<T> x, List<T> y) {
    if (x.size() != y.size())
      throw new RuntimeException("Elements to compare not of same size");
    Set<OrderComparison> comparisonSet = IntStream.range(0, x.size()) //
        .mapToObj(index -> comparatorList.get(index).compare(x.get(index), y.get(index))) //
        .collect(Collectors.toSet());
    if (elementsIncomparable(comparisonSet))
      return OrderComparison.INCOMPARABLE;
    else if (comparisonSet.contains(OrderComparison.STRICTLY_PRECEDES))
      return OrderComparison.STRICTLY_PRECEDES;
    else if (comparisonSet.contains(OrderComparison.STRICTLY_SUCCEEDS))
      return OrderComparison.STRICTLY_SUCCEEDS;
    else
      return OrderComparison.INDIFFERENT;
  }

  /** The product order is incomparable if any two elements of x and y are incomparable or
   * if strict precedence and strict succession are present
   * 
   * @param comparisonSet
   * @return true if product order is incomparable, false otherwise */
  public boolean elementsIncomparable(Set<OrderComparison> comparisonSet) {
    return comparisonSet.contains(OrderComparison.INCOMPARABLE) //
        || (comparisonSet.contains(OrderComparison.STRICTLY_SUCCEEDS) && comparisonSet.contains(OrderComparison.STRICTLY_PRECEDES));
  }
}
