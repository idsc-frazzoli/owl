// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public enum ProductTotalOrder implements PartialComparator<List<Comparable>> {
  INSTANCE,//
  ;
  @Override
  public PartialComparison compare(List<Comparable> x, List<Comparable> y) {
    Set<Integer> toSet = IntStream.range(0, x.size()).map(index -> x.get(index).compareTo(y.get(index))).boxed().collect(Collectors.toSet());
    if (toSet.containsAll(Arrays.asList(1, -1))) {
      return PartialComparison.INCOMPARABLE;
    }
    if (toSet.contains(1)) {
      return PartialComparison.GREATER_THAN;
    }
    if (toSet.contains(-1)) {
      return PartialComparison.LESS_THAN;
    }
    return PartialComparison.EQUALS;
  }
}
