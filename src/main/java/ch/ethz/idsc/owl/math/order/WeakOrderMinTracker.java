// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.util.LinkedList;
import java.util.List;

/** Creates a list of minimal elements of a weakly ordered set <tt>X</tt>.
 * TODO definition minimal element
 * 
 * @param <T> type of elements to compare */
public class WeakOrderMinTracker<T> {
  public final WeakOrderComparator<T> comparator;
  public final List<T> list = new LinkedList<>();

  public WeakOrderMinTracker(WeakOrderComparator<T> comparator) {
    this.comparator = comparator;
  }

  /** Compares an element <tt>x</tt> of a partial order to the current set of minimal elements.
   *
   * <p>The element gets added to the list if it precedes any of the current elements of the set.
   * 
   * @param x Element next up for comparison */
  public void digest(T x) {
    if (!list.isEmpty()) {
      T anyElement = list.get(0);
      WeakOrderComparison weakComparison = comparator.compare(x, anyElement);
      if (weakComparison.equals(WeakOrderComparison.LESS_EQUALS_ONLY)) {
        list.removeAll(list);
        // TODO ANDRE better way to empty the whole list
      } else if (weakComparison.equals(WeakOrderComparison.GREATER_EQUALS_ONLY)) {
        return;
      }
    }
    // TODO ANDRE neater way to check for duplicates
    if (!list.contains(x)) {
      list.add(x);
    }
  }

  /** @return Minimal elements of partially ordered set */
  public List<T> getMinElements() {
    return list;
  }
}
