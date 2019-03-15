// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

/** @param <T> type of elements to compare */
public class PreorderMinTracker<T> extends AbstractPreorderMinTracker<T> {
  public static <T> PreorderMinTracker<T> withList(PreorderComparator<T> preorderComparator) {
    return new PreorderMinTracker<>(preorderComparator, new LinkedList<>());
  }

  public static <T> PreorderMinTracker<T> withSet(PreorderComparator<T> preorderComparator) {
    return new PreorderMinTracker<>(preorderComparator, new HashSet<>());
  }

  private PreorderMinTracker(PreorderComparator<T> preorderComparator, Collection<T> collection) {
    super(preorderComparator, collection);
  }

  @Override
  public boolean discardCriterion(PreorderComparison preorderComparison) {
    // TODO Auto-generated method stub
    return preorderComparison.equals(PreorderComparison.GREATER_EQUALS_ONLY);
  }
}
