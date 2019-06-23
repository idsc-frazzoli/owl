//code by gjoel
package ch.ethz.idsc.owl.rrts.adapter;

import ch.ethz.idsc.owl.rrts.core.Transition;
import ch.ethz.idsc.owl.rrts.core.TransitionSpace;
import ch.ethz.idsc.tensor.Scalar;

public abstract class AbstractTransitionSpace implements TransitionSpace {
  @Override // from TransitionSpace
  public Scalar distance(Transition transition) {
    return distance(transition.start(), transition.end());
  }
}
