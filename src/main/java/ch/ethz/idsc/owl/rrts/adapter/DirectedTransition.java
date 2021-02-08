// code by gjoel
package ch.ethz.idsc.owl.rrts.adapter;

import ch.ethz.idsc.owl.rrts.core.Transition;
import ch.ethz.idsc.owl.rrts.core.TransitionWrap;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public class DirectedTransition extends AbstractTransition {
  protected final Transition transition;
  public final boolean isForward;

  /* package */ DirectedTransition(Transition transition, boolean isForward) {
    super( //
        isForward //
            ? transition.start() //
            : transition.end(), //
        isForward //
            ? transition.end() //
            : transition.start(), //
        transition.length());
    this.transition = transition;
    this.isForward = isForward;
  }

  @Override // from Transition
  public Tensor sampled(Scalar minResolution) {
    return transition.sampled(minResolution);
  }

  @Override // from Transition
  public TransitionWrap wrapped(Scalar minResolution) {
    return transition.wrapped(minResolution);
  }

  @Override // from Transition
  public Tensor linearized(Scalar minResolution) {
    return transition.linearized(minResolution);
  }
}
