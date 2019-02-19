// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Objects;

/** Creates a list of minimal elements of a weakly ordered set <tt>X</tt>.
 * TODO definition minimal element
 * 
 * @param <T> type of elements to compare */
public class WeakOrderMinTracker<T> implements MinTrackerInterface<T> {
  public static <T> WeakOrderMinTracker<T> withList(WeakOrderComparator<T> weakOrderComparator) {
    return new WeakOrderMinTracker<>(weakOrderComparator, new LinkedList<>());
  }

  public static <T> WeakOrderMinTracker<T> withSet(WeakOrderComparator<T> weakOrderComparator) {
    return new WeakOrderMinTracker<>(weakOrderComparator, new HashSet<>());
  }

  // ---
  private final WeakOrderComparator<T> weakOrderComparator;
  private final Collection<T> collection;

  private WeakOrderMinTracker(WeakOrderComparator<T> weakOrderComparator, Collection<T> collection) {
    this.weakOrderComparator = Objects.requireNonNull(weakOrderComparator);
    this.collection = collection;
  }

  /** Compares an element <tt>x</tt> of a partial order to the current set of minimal elements.
   *
   * <p>The element gets added to the list if it precedes any of the current elements of the set.
   * 
   * @param x Element next up for comparison */
  @Override // from MinTrackerInterface
  public void digest(T x) {
    if (collection.isEmpty())
      collection.add(x);
    else
      switch (weakOrderComparator.compare(x, collection.iterator().next())) {
      case LESS_EQUALS_ONLY:
        collection.clear();
        collection.add(x);
        break;
      case INDIFFERENT:
        if (!collection.contains(x))
          collection.add(x);
        break;
      case GREATER_EQUALS_ONLY:
        // <- ignore given x
        break;
      }
  }

  /** @return Minimal elements of partially ordered set */
  @Override // from MinTrackerInterface
  public Collection<T> getMinElements() {
    return Collections.unmodifiableCollection(collection);
  }
}
