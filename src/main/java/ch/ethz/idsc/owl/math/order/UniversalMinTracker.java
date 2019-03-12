package ch.ethz.idsc.owl.math.order;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;

public class UniversalMinTracker<T> implements MinTrackerInterface<T> {
  public static <T> UniversalMinTracker<T> withList(UniversalComparator<T> universalComparator) {
    return new UniversalMinTracker<>(universalComparator, new LinkedList<>());
  }

  public static <T> UniversalMinTracker<T> withSet(UniversalComparator<T> universalComparator) {
    return new UniversalMinTracker<>(universalComparator, new HashSet<>());
  }

  private final UniversalComparator<T> comparator;
  private final Collection<T> collection;

  protected UniversalMinTracker(UniversalComparator<T> comparator, Collection<T> collection) {
    this.comparator = Objects.requireNonNull(comparator);
    this.collection = collection;
  }

  @Override
  public void digest(T x) {
    Iterator<T> iterator = collection.iterator();
    while (iterator.hasNext()) {
      T b = iterator.next();
      UniversalComparison comparison = comparator.compare(x, b);
      if (comparison.equals(UniversalComparison.STRICTLY_PRECEDES))
        iterator.remove();
      else //
      if (criterion(comparison))
        return;
    }
    if (!collection.contains(x)) {
      collection.add(x);
    }
  }

  public boolean criterion(UniversalComparison comparison) {
    // TODO Auto-generated method stub
    return comparison.equals(UniversalComparison.STRICTLY_PRECEDES) || comparison.equals(UniversalComparison.INDIFFERENT);
  }

  @Override
  public Collection<T> getMinElements() {
    // TODO Auto-generated method stub
    return null;
  }
}
