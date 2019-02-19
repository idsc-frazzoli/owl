// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;

/** Creates a list of minimal elements of a partially ordered set <tt>X</tt>.
 * <p>An element <tt>a</tt> in a partial order is a minimal element if for all <tt>b</tt> in <tt>X</tt>,
 * <tt>b</tt> &le <tt>a</tt> does NOT hold.
 * 
 * @param <T> type of elements to compare */
public class PartialOrderMinTracker<T> implements MinTrackerInterface<T> {
  public static <T> PartialOrderMinTracker<T> withList(PartialComparator<T> partialComparator) {
    return new PartialOrderMinTracker<>(partialComparator, new LinkedList<>());
  }

  public static <T> PartialOrderMinTracker<T> withSet(PartialComparator<T> partialComparator) {
    return new PartialOrderMinTracker<>(partialComparator, new HashSet<>());
  }

  // ---
  private final PartialComparator<T> partialComparator;
  private final Collection<T> collection;

  private PartialOrderMinTracker(PartialComparator<T> partialComparator, Collection<T> collection) {
    this.partialComparator = Objects.requireNonNull(partialComparator);
    this.collection = collection;
  }

  /** Compares an element <tt>x</tt> of a partial order to the current set of minimal elements.
   *
   * <p>The element gets added to the list if it precedes any of the current elements of the set.
   * 
   * @param x Element next up for comparison */
  @Override // from MinTrackerInterface
  public void digest(T x) {
    Iterator<T> iterator = collection.iterator();
    while (iterator.hasNext()) {
      T b = iterator.next();
      PartialComparison partialComparison = partialComparator.compare(x, b);
      if (partialComparison.equals(PartialComparison.LESS_THAN))
        iterator.remove();
      else //
      if (!partialComparison.equals(PartialComparison.INCOMPARABLE)) // implies "equals", or "greater than"
        return;
    }
    collection.add(x);
  }

  /** @return Minimal elements of partially ordered set */
  @Override // from MinTrackerInterface
  public Collection<T> getMinElements() {
    return Collections.unmodifiableCollection(collection);
  }
}
