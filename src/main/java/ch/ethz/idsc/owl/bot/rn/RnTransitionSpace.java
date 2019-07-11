// code by jph, gjoel
package ch.ethz.idsc.owl.bot.rn;

import java.io.Serializable;

import ch.ethz.idsc.owl.rrts.core.TransitionSpace;
import ch.ethz.idsc.tensor.Tensor;

public class RnTransitionSpace implements TransitionSpace, Serializable {
  public static final TransitionSpace INSTANCE = new RnTransitionSpace();

  private RnTransitionSpace() {
    // ---
  }

  @Override // from TransitionSpace
  public RnTransition connect(Tensor start, Tensor end) {
    return new RnTransition(start, end);
  }
}
