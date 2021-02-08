// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;

/** Tracks minimal elements of a transitive ordered set <tt>X</tt>.
 * An element x is said to be minimal if there is no other element y such that yRx.
 * (Strict) Total orders, total preorders and weak orders, preorders, semiorders are all transitive.
 * Be aware that negatively transitive orders are transitive as well and
 * thus work for this MinTracker but with significant performance loss.
 * 
 * @param <T> type of elements to compare */
public class TransitiveMinTracker<T> implements MinTracker<T>, Serializable {
  /** @param <T>
   * @param orderComparator
   * @return */
  public static <T> MinTracker<T> withList(OrderComparator<T> orderComparator) {
    return new TransitiveMinTracker<>(orderComparator, new LinkedList<>());
  }

  /** @param <T>
   * @param orderComparator
   * @return */
  public static <T> MinTracker<T> withSet(OrderComparator<T> orderComparator) {
    return new TransitiveMinTracker<>(orderComparator, new HashSet<>());
  }

  /***************************************************/
  private final OrderComparator<T> orderComparator;
  private final Collection<T> collection;

  protected TransitiveMinTracker(OrderComparator<T> orderComparator, Collection<T> collection) {
    this.orderComparator = Objects.requireNonNull(orderComparator);
    this.collection = collection;
  }

  @Override // from MinTracker
  public final void digest(T x) {
    Iterator<T> iterator = collection.iterator();
    while (iterator.hasNext()) {
      T b = iterator.next();
      OrderComparison orderComparison = orderComparator.compare(x, b);
      if (orderComparison.equals(OrderComparison.STRICTLY_PRECEDES))
        iterator.remove();
      else //
      if (discardCriterion(orderComparison))
        return;
    }
    if (!collection.contains(x))
      collection.add(x);
  }

  /** Discards elements which strictly succeed any of the current elements.
   * 
   * @param orderComparison
   * @return */
  protected boolean discardCriterion(OrderComparison orderComparison) {
    return orderComparison.equals(OrderComparison.STRICTLY_SUCCEEDS);
  }

  /** @return minimal elements of partially ordered set */
  @Override // from MinTracker
  public final Collection<T> getMinElements() {
    return Collections.unmodifiableCollection(collection);
  }
}
