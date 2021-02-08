// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

/** Tracks minimal elements of a transitive ordered set <tt>X</tt>.
 * An element x is said to be minimal if there is no other element y such that yRx.
 * (Strict) Total orders, total preorders and weak orders, preorders, semiorders are all transitive.
 * 
 * Be aware that neg. transitive orders are transitive as well and
 * thus work for this MinTracker but with significant performance loss.
 * 
 * Maintains complete set of representatives of the minimal equivalence classes.
 * 
 * @param <T> type of elements to compare */
public class RepresentativeNegTransitiveMinTracker<T> extends NegTransitiveMinTracker<T> {
  public static <T> MinTracker<T> withList(OrderComparator<T> orderComparator) {
    return new RepresentativeNegTransitiveMinTracker<>(orderComparator, new LinkedList<>());
  }

  public static <T> MinTracker<T> withSet(OrderComparator<T> orderComparator) {
    return new RepresentativeNegTransitiveMinTracker<>(orderComparator, new HashSet<>());
  }

  /***************************************************/
  private RepresentativeNegTransitiveMinTracker(OrderComparator<T> orderComparator, Collection<T> collection) {
    super(orderComparator, collection);
  }

  /** Sets whether or not indifferent or incomparable elements shall be kept as well.
   * 
   * @param comparison
   * @return true if element to be discarded or false otherwise */
  @Override
  protected boolean keepOnlyRepresentatives() {
    return true;
  }
}
