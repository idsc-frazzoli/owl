// code by jph
package ch.ethz.idsc.owl.bot.rn;

import ch.ethz.idsc.owl.rrts.core.Transition;
import ch.ethz.idsc.owl.rrts.core.TransitionSpace;
import ch.ethz.idsc.tensor.Tensor;

public enum RnTransitionSpace implements TransitionSpace {
  INSTANCE;
  // ---
  @Override // from TransitionSpace
  public Transition connect(Tensor start, Tensor end) {
    return new RnTransition(start, end);
  }
}
