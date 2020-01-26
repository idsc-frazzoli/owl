// code by jph
package ch.ethz.idsc.owl.rrts.adapter;

import ch.ethz.idsc.owl.rrts.core.Transition;
import ch.ethz.idsc.owl.rrts.core.TransitionRegionQuery;

public enum EmptyTransitionRegionQuery implements TransitionRegionQuery {
  INSTANCE;

  // ---
  @Override // from TransitionRegionQuery
  public boolean isDisjoint(Transition transition) {
    return true;
  }
}
