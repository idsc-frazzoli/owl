package ch.ethz.idsc.owl.math.order;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;
/** Tracks minimal elements of a transitive ordered set <tt>X</tt>.
 * An element x is said to be minimal if there is no other element y such that yRx.
 * (Strict) Total orders, total preorders and weak orders, preorders, semiorders are all transitive.
 * Be aware that neg. transitive orders are transitive as well and 
 * thus work for this MinTracker but with significant performance losses.
 * @param <T> type of elements to compare */
public class TransitiveMinTracker<T> implements MinTracker<T>{

 
  public static <T> TransitiveMinTracker<T> withList(OrderComparator<T> orderComparator) {
    return new TransitiveMinTracker<>(orderComparator, new LinkedList<>());
  }

  public static <T> TransitiveMinTracker<T> withSet(OrderComparator<T> orderComparator) {
    return new TransitiveMinTracker<>(orderComparator, new HashSet<>());
  }

  private final OrderComparator<T> comparator;
  private final Collection<T> collection;

  protected TransitiveMinTracker(OrderComparator<T> comparator, Collection<T> collection) {
    this.comparator = Objects.requireNonNull(comparator);
    this.collection = collection;
  }

  @Override
  public final void digest(T x) {
    Iterator<T> iterator = collection.iterator();
    while (iterator.hasNext()) {
      T b = iterator.next();
      OrderComparison comparison = comparator.compare(x, b);
      if (comparison.equals(OrderComparison.STRICTLY_PRECEDES))
        iterator.remove();
      else //
      if (discardCriterion(comparison))
        return;
    }
    if (!collection.contains(x)) {
      collection.add(x);
    }
  }
  /**Discards elements which strictly succeed any of the current elements.
   * 
   * @param comparison
   * @return
   */
  protected boolean discardCriterion(OrderComparison comparison) {
    return comparison.equals(OrderComparison.STRICTLY_SUCCEEDS);
  }


  /** @return Minimal elements of partially ordered set */
  @Override // from MinTrackerInterface
  public Collection<T> getMinElements() {
    return Collections.unmodifiableCollection(collection);
  }
}
