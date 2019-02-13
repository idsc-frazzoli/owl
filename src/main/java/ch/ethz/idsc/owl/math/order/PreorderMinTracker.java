// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

/** Creates a list of minimal elements of a preordered set <tt>X</tt>.
 * <p>An element <tt>a</tt> in a preorder is a minimal element if for all <tt>b</tt> in <tt>X</tt>,
 * <tt>a</tt> &le <tt>b</tt> and NOT <tt>b</tt> &le <tt>a</tt> holds.
 * TODO new definition of minimal element
 * 
 * @param <T> type of elements to compare */
public class PreorderMinTracker<T> implements MinTrackerInterface<T> {
  public static <T> PreorderMinTracker<T> withList(PreorderComparator<T> preorderComparator) {
    return new PreorderMinTracker<>(preorderComparator, new LinkedList<>());
  }

  public static <T> PreorderMinTracker<T> withSet(PreorderComparator<T> preorderComparator) {
    return new PreorderMinTracker<>(preorderComparator, new HashSet<>());
  }

  private final PreorderComparator<T> preorderComparator;
  private final Collection<T> collection;

  private PreorderMinTracker(PreorderComparator<T> preorderComparator, Collection<T> collection) {
    this.preorderComparator = preorderComparator;
    this.collection = collection;
  }

  /** Compares an element <tt>x</tt> of a preorder to the current set of minimal elements.
   *
   * <p>The element gets added to the list if it precedes any of the current elements of the set.
   * 
   * @param x Element next up for comparison */
  @Override // from MinTrackerInterface
  public void digest(T x) {
    Iterator<T> iterator = collection.iterator();
    while (iterator.hasNext()) {
      T b = iterator.next();
      PreorderComparison preorderComparison = preorderComparator.compare(x, b);
      if (preorderComparison.equals(PreorderComparison.LESS_EQUALS_ONLY)) {
        iterator.remove();
      } else if (preorderComparison.equals(PreorderComparison.GREATER_EQUALS_ONLY)) {
        return;
      }
    }
    if (!collection.contains(x)) {
      collection.add(x);
    }
  }

  /** @return Minimal elements of preordered set */
  @Override // from MinTrackerInterface
  public Collection<T> getMinElements() {
    return Collections.unmodifiableCollection(collection);
  }
}
