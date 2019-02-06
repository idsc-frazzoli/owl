// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/** Creates a list of minimal elements of a preordered set <tt>X</tt>.
 * <p>An element <tt>a</tt> in a preorder is a minimal element if for all <tt>b</tt> in <tt>X</tt>,
 * <tt>a</tt> &le <tt>b</tt> and NOT <tt>b</tt> &le <tt>a</tt> holds.
 * TODO new definition of minimal element
 * 
 * @param <T> type of elements to compare */
public class PreorderMinTracker<T> {
  public final PreorderComparator<T> comparator;
  // FIXME list allows for duplicate elements and ordering within list is crucial maybe switch to Set
  public final List<T> list = new LinkedList<>();

  public PreorderMinTracker(PreorderComparator<T> comparator) {
    this.comparator = comparator;
  }

  /** Compares an element <tt>x</tt> of a preorder to the current set of minimal elements.
   *
   * <p>The element gets added to the list if it precedes any of the current elements of the set.
   * 
   * @param x Element next up for comparison */
  public void digest(T x) {
    Iterator<T> iterator = list.iterator();
    while (iterator.hasNext()) {
      T b = iterator.next();
      PreorderComparison preorderComparison = comparator.compare(x, b);
      if (preorderComparison.equals(PreorderComparison.LESS_EQUALS_ONLY)) {
        iterator.remove();
      } else if (preorderComparison.equals(PreorderComparison.GREATER_EQUALS_ONLY)) {
        return;
      }
    }
    list.add(x);
  }

  /** @return Minimal elements of preordered set */
  public List<T> getMinElements() {
    return list;
  }
}
