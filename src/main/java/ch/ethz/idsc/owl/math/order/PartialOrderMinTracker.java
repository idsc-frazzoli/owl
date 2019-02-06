// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/** Creates a list of minimal elements of a partially ordered set <tt>X</tt>.
 * <p>An element <tt>a</tt> in a partial order is a minimal element if for all <tt>b</tt> in <tt>X</tt>,
 * <tt>b</tt> &le <tt>a</tt> does NOT hold.
 * 
 * @param <T> type of elements to compare */
public class PartialOrderMinTracker<T> {
  public final PartialComparator<T> comparator;
  public final List<T> list = new LinkedList<>();

  public PartialOrderMinTracker(PartialComparator<T> comparator) {
    this.comparator = comparator;
  }

  /** Compares an element <tt>x</tt> of a partial order to the current set of minimal elements.
   *
   * <p>The element gets added to the list if it precedes any of the current elements of the set.
   * 
   * @param x Element next up for comparison */
  public void digest(T x) {
    Iterator<T> iterator = list.iterator();
    while (iterator.hasNext()) {
      T b = iterator.next();
      PartialComparison partialComparison = comparator.compare(x, b);
      if (partialComparison.equals(PartialComparison.LESS_THAN)) {
        iterator.remove();
      } else if (!partialComparison.equals(PartialComparison.INCOMPARABLE)) {
        // ALTERNATIVELY
        // else if (partialComparison.equals(PartialComparison.GREATER_THAN)&&partialComparison.equals(PartialComparison.EQUALS)) {
        return;
      }
    }
    list.add(x);
  }

  /** @return Minimal elements of partially ordered set */
  public List<T> getMinElements() {
    return list;
  }
}
