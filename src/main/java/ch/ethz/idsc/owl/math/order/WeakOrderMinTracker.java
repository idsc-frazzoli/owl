// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;

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

  private final WeakOrderComparator<T> weakOrderComparator;
  private final Collection<T> collection;

  private WeakOrderMinTracker(WeakOrderComparator<T> weakOrderComparator, Collection<T> collection) {
    this.weakOrderComparator = weakOrderComparator;
    this.collection = collection;
  }

  /** Compares an element <tt>x</tt> of a partial order to the current set of minimal elements.
   *
   * <p>The element gets added to the list if it precedes any of the current elements of the set.
   * 
   * @param x Element next up for comparison */
  @Override // from MinTrackerInterface
  public void digest(T x) {
    if (!collection.isEmpty()) {
      T anyElement = collection.iterator().next();
      WeakOrderComparison weakComparison = weakOrderComparator.compare(x, anyElement);
      if (weakComparison.equals(WeakOrderComparison.LESS_EQUALS_ONLY)) {
        collection.clear();
      } else if (weakComparison.equals(WeakOrderComparison.GREATER_EQUALS_ONLY)) {
        return;
      }
    }
    // TODO ANDRE neater way to check for duplicates
    // list.contains(x) only uses a.equals(b)
    // set.contains(x) only uses object.hashCode() and a.equals(b)
    // ... not sure if API should require types to provide hashCode
    // -> probably yes so that we can use HashSet instead of LinkedList
    if (!collection.contains(x)) {
      collection.add(x);
    }
  }

  /** @return Minimal elements of partially ordered set */
  @Override // from MinTrackerInterface
  public Collection<T> getMinElements() {
    return Collections.unmodifiableCollection(collection);
  }
}
