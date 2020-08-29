// code by gjoel
package ch.ethz.idsc.owl.rrts.adapter;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;

import ch.ethz.idsc.owl.rrts.core.Transition;
import ch.ethz.idsc.owl.rrts.core.TransitionRegionQuery;

public class TransitionRegionQueryUnion implements TransitionRegionQuery, Serializable {
  /** @param queries
   * @return */
  public static TransitionRegionQuery wrap(TransitionRegionQuery... queries) {
    return wrap(Arrays.asList(queries));
  }

  /** @param collection
   * @return */
  public static TransitionRegionQuery wrap(Collection<TransitionRegionQuery> collection) {
    return new TransitionRegionQueryUnion(collection);
  }

  /***************************************************/
  private final Collection<TransitionRegionQuery> collection;

  private TransitionRegionQueryUnion(Collection<TransitionRegionQuery> collection) {
    this.collection = collection;
  }

  @Override // from TransitionRegionQuery
  public boolean isDisjoint(Transition transition) {
    return collection.stream().allMatch(transitionRegionQuery -> transitionRegionQuery.isDisjoint(transition));
  }
}
