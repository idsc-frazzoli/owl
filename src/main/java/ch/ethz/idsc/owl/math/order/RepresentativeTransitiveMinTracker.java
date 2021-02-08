// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

/** Tracks minimal elements of a transitive ordered set <tt>X</tt>.
 * An element x is said to be minimal if there is no other element y such that yRx.
 * (Strict) Total orders, total preorders and weak orders, preorders, semiorders are all transitive.
 * Be aware that negatively transitive orders are transitive as well and
 * thus work for this MinTracker but with significant performance loss.
 * 
 * Maintains complete set of representatives of the minimal equivalence classes.
 * 
 * @param <T> type of elements to compare */
public class RepresentativeTransitiveMinTracker<T> extends TransitiveMinTracker<T> {
  /** @param orderComparator
   * @return */
  public static <T> MinTracker<T> withList(OrderComparator<T> orderComparator) {
    return new RepresentativeTransitiveMinTracker<>(orderComparator, new LinkedList<>());
  }

  public static <T> MinTracker<T> withSet(OrderComparator<T> orderComparator) {
    return new RepresentativeTransitiveMinTracker<>(orderComparator, new HashSet<>());
  }

  /***************************************************/
  private RepresentativeTransitiveMinTracker(OrderComparator<T> orderComparator, Collection<T> collection) {
    super(orderComparator, collection);
  }

  /** Discards elements which strictly succeed or are indifferent to any of the current elements.
   * 
   * @param comparison
   * @return true if element to be discarded or false otherwise */
  @Override // from TransitiveMinTracker
  protected boolean discardCriterion(OrderComparison orderComparison) {
    return orderComparison.equals(OrderComparison.STRICTLY_SUCCEEDS) //
        || orderComparison.equals(OrderComparison.INDIFFERENT);
  }
}
