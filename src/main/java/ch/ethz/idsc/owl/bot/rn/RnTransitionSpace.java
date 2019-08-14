// code by jph, gjoel
package ch.ethz.idsc.owl.bot.rn;

import ch.ethz.idsc.owl.rrts.core.TransitionSpace;
import ch.ethz.idsc.tensor.Tensor;

public enum RnTransitionSpace implements TransitionSpace {
  INSTANCE;
  // ---
  @Override // from TransitionSpace
  public RnTransition connect(Tensor start, Tensor end) {
    return new RnTransition(start, end);
  }
}
