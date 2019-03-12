// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

/** @param <T> type of elements to compare */
public class PreorderRepresentativeMinTracker<T> extends AbstractPreorderMinTracker<T> {
  public static <T> PreorderRepresentativeMinTracker<T> withList(PreorderComparator<T> preorderComparator) {
    return new PreorderRepresentativeMinTracker<>(preorderComparator, new LinkedList<>());
  }

  public static <T> PreorderRepresentativeMinTracker<T> withSet(PreorderComparator<T> preorderComparator) {
    return new PreorderRepresentativeMinTracker<>(preorderComparator, new HashSet<>());
  }

  private PreorderRepresentativeMinTracker(PreorderComparator<T> preorderComparator, Collection<T> collection) {
    super(preorderComparator, collection);
  }

  @Override
  public boolean criterion(PreorderComparison preorderComparison) {
    // TODO Auto-generated method stub
    return preorderComparison.equals(PreorderComparison.GREATER_EQUALS_ONLY) || preorderComparison.equals(PreorderComparison.INDIFFERENT);
  }
}
