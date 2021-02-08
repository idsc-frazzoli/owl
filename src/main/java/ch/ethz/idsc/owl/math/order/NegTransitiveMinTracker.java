// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Objects;

/** Tracks minimal elements of a negatively transitive ordered set <tt>X</tt>.
 * An element x is said to be minimal if there is no other element y such that yRx.
 * (Strict) Total orders, total preorders and weak orders are all negatively transitive.
 * For non-strict negatively transitive orders the indifference constitutes equivalence
 * classes, and for strict negatively transitive orders incomparability.
 * 
 * @param <T> type of elements to compare */
public class NegTransitiveMinTracker<T> implements MinTracker<T>, Serializable {
  /** @param <T>
   * @param orderComparator
   * @return */
  public static <T> MinTracker<T> withList(OrderComparator<T> orderComparator) {
    return new NegTransitiveMinTracker<>(orderComparator, new LinkedList<>());
  }

  /** @param <T>
   * @param orderComparator
   * @return */
  public static <T> MinTracker<T> withSet(OrderComparator<T> orderComparator) {
    return new NegTransitiveMinTracker<>(orderComparator, new HashSet<>());
  }

  /***************************************************/
  private final OrderComparator<T> orderComparator;
  private final Collection<T> collection;

  protected NegTransitiveMinTracker(OrderComparator<T> orderComparator, Collection<T> collection) {
    this.orderComparator = Objects.requireNonNull(orderComparator);
    this.collection = collection;
  }

  /** Compares an element <tt>x</tt> of a negatively transitive order to the current set of minimal elements.
   *
   * <p>If an element precedes any of the current elements of the set, the set of minimal elements is emptied and
   * the element gets added to the list.
   * 
   * @param x Element next up for comparison */
  @Override // from MinTrackerInterface
  public final void digest(T x) {
    if (collection.isEmpty())
      collection.add(x);
    else
      switch (orderComparator.compare(x, collection.iterator().next())) {
      case STRICTLY_PRECEDES:
        collection.clear();
        collection.add(x);
        break;
      // in case of total preorder or total order
      case INDIFFERENT:
        if (!collection.contains(x) && //
            !keepOnlyRepresentatives())
          collection.add(x);
        break;
      // TODO ASTOLL justify why this is the same as "case indifferent"
      // in case of strict weak order or strict total order
      case INCOMPARABLE:
        if (!collection.contains(x) && //
            !keepOnlyRepresentatives())
          collection.add(x);
        break;
      case STRICTLY_SUCCEEDS:
        // <- ignore given x
        break;
      }
  }

  /** Sets whether or not indifferent or incomparable elements shall be kept as well.
   * 
   * @param comparison
   * @return true if element to be discarded or false otherwise */
  protected boolean keepOnlyRepresentatives() {
    return false;
  }

  @Override // from MinTrackerInterface
  public final Collection<T> getMinElements() {
    return Collections.unmodifiableCollection(collection);
  }
}
