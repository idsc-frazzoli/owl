// code by gjoel
package ch.ethz.idsc.owl.rrts.adapter;

import java.util.Arrays;
import java.util.Collection;

import ch.ethz.idsc.owl.rrts.core.Transition;
import ch.ethz.idsc.owl.rrts.core.TransitionRegionQuery;

public class UnionTransitionRegionQuery implements TransitionRegionQuery {
  public static TransitionRegionQuery wrap(TransitionRegionQuery... queries) {
    return wrap(Arrays.asList(queries));
  }

  public static TransitionRegionQuery wrap(Collection<TransitionRegionQuery> collection) {
    return new UnionTransitionRegionQuery(collection);
  }
  // ---
  private final Collection<TransitionRegionQuery> collection;

  private UnionTransitionRegionQuery(Collection<TransitionRegionQuery> collection) {
    this.collection = collection;
  }

  @Override // from TransitionRegionQuery
  public boolean isDisjoint(Transition transition) {
    return collection.stream().allMatch(transitionRegionQuery -> transitionRegionQuery.isDisjoint(transition));
  }
}
