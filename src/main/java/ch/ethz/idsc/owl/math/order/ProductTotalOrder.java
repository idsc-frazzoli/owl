// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SuppressWarnings("rawtypes")
public enum ProductTotalOrder implements OrderComparator<List<Comparable>> {
  INSTANCE;

  private static final List<Integer> PLUS_MINUS = Arrays.asList(1, -1);

  @Override // from PartialComparator
  public OrderComparison compare(List<Comparable> x, List<Comparable> y) {
    @SuppressWarnings("unchecked")
    Set<Integer> set = IntStream.range(0, Math.max(x.size(), y.size())) //
        .map(index -> x.get(index).compareTo(y.get(index))) //
        .map(Integer::signum) //
        .boxed() //
        .collect(Collectors.toSet());
    if (set.containsAll(PLUS_MINUS))
      return OrderComparison.INCOMPARABLE;
    if (set.contains(1))
      return OrderComparison.STRICTLY_SUCCEEDS;
    if (set.contains(-1))
      return OrderComparison.STRICTLY_PRECEDES;
    return OrderComparison.INDIFFERENT;
  }
}
