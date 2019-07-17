// code by jph, gjoel
package ch.ethz.idsc.owl.bot.se2.rrts;

import ch.ethz.idsc.tensor.Tensor;

public enum ClothoidTransitionSpace implements Se2TransitionSpace {
  INSTANCE;
  // ---
  @Override // from TransitionSpace
  public ClothoidTransition connect(Tensor start, Tensor end) {
    return new ClothoidTransition(start, end);
  }
}
