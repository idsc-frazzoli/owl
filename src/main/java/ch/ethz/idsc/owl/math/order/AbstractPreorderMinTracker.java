// code by astoll
package ch.ethz.idsc.owl.math.order;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;

public abstract class AbstractPreorderMinTracker<T> implements MinTrackerInterface<T> {
  private final PreorderComparator<T> preorderComparator;
  private final Collection<T> collection;

  protected AbstractPreorderMinTracker(PreorderComparator<T> preorderComparator, Collection<T> collection) {
    this.preorderComparator = Objects.requireNonNull(preorderComparator);
    this.collection = collection;
  }

  /** Depending on whether we only want all indifferent solutions or just a complete set of representatives
   * set the discarding criterion accordingly
   * 
   * @param preorderComparison
   * @return true or false
   */
  protected abstract boolean discardCriterion(PreorderComparison preorderComparison);

  /** Compares an element <tt>x</tt> of a preorder to the current set of minimal elements.
   *
   * <p>The element gets added to the list if it precedes any of the current elements of the set.
   * 
   * @param x Element next up for comparison */
  @Override // from MinTrackerInterface
  public final void digest(T x) {
    Iterator<T> iterator = collection.iterator();
    while (iterator.hasNext()) {
      T b = iterator.next();
      PreorderComparison preorderComparison = preorderComparator.compare(x, b);
      if (preorderComparison.equals(PreorderComparison.LESS_EQUALS_ONLY))
        iterator.remove();
      else //
      if (discardCriterion(preorderComparison))
        return;
    }
    if (!collection.contains(x)) {
      collection.add(x);
    }
  }

  /** @return Minimal elements of preordered set */
  @Override // from MinTrackerInterface
  public final Collection<T> getMinElements() {
    return Collections.unmodifiableCollection(collection);
  }
}
