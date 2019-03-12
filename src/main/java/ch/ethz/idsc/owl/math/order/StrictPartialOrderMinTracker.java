// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

/** Creates a list of minimal elements of a strict partially ordered set <tt>X</tt>.
 * <p>An element <tt>a</tt> in a partial order is a minimal element if for all <tt>b</tt> in <tt>X</tt>,
 * <tt>b</tt> &le <tt>a</tt> does NOT hold.
 * 
 * @param <T> type of elements to compare */
public class StrictPartialOrderMinTracker<T> implements MinTrackerInterface<T> {
  public static <T> StrictPartialOrderMinTracker<T> withList(StrictPartialComparator<T> strictPartialComparator) {
    return new StrictPartialOrderMinTracker<>(strictPartialComparator, new LinkedList<>());
  }

  public static <T> StrictPartialOrderMinTracker<T> withSet(StrictPartialComparator<T> strictPartialComparator) {
    return new StrictPartialOrderMinTracker<>(strictPartialComparator, new HashSet<>());
  }

  private final StrictPartialComparator<T> strictPartialComparator;
  private final Collection<T> collection;

  private StrictPartialOrderMinTracker(StrictPartialComparator<T> strictPartialComparator, Collection<T> collection) {
    this.strictPartialComparator = strictPartialComparator;
    this.collection = collection;
  }

  /** Compares an element <tt>x</tt> of a strict partial order to the current set of minimal elements.
   *
   * <p>The element gets added to the list if it precedes any of the current elements of the set.
   * 
   * @param x Element next up for comparison */
  @Override // from MinTrackerInterface
  public void digest(T x) {
    Iterator<T> iterator = collection.iterator();
    while (iterator.hasNext()) {
      T b = iterator.next();
      StrictPartialComparison strictPartialComparison = strictPartialComparator.compare(x, b);
      if (strictPartialComparison.equals(StrictPartialComparison.LESS_THAN))
        iterator.remove();
      else //
      if (strictPartialComparison.equals(StrictPartialComparison.GREATER_THAN))
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
