// code by jph, gjoel
package ch.ethz.idsc.owl.bot.se2.rrts;

import ch.ethz.idsc.owl.rrts.core.TransitionSpace;
import ch.ethz.idsc.tensor.Tensor;

/** start and end points are from SE(2) */
public enum ClothoidTransitionSpace implements TransitionSpace {
  INSTANCE;

  @Override // from TransitionSpace
  public ClothoidTransition connect(Tensor start, Tensor end) {
    return ClothoidTransition.of(start, end);
  }
}
