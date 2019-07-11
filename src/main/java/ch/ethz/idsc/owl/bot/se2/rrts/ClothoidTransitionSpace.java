// code by jph, gjoel
package ch.ethz.idsc.owl.bot.se2.rrts;

import java.io.Serializable;

import ch.ethz.idsc.owl.rrts.core.TransitionSpace;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public class ClothoidTransitionSpace implements Se2TransitionSpace, Serializable {
  public static final TransitionSpace INSTANCE = new ClothoidTransitionSpace();
  // ---

  private ClothoidTransitionSpace() {
    // ---
  }

  @Override // from TransitionSpace
  public ClothoidTransition connect(Tensor start, Tensor end) {
    return new ClothoidTransition(start, end);
  }

  @Override // from TransitionSpace
  public Scalar distance(Tensor start, Tensor end) {
    return ClothoidTransition.TENSOR_METRIC.distance(start, end);
  }
}
