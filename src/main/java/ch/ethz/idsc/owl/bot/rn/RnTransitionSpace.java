// code by jph, gjoel
package ch.ethz.idsc.owl.bot.rn;

import java.io.Serializable;

import ch.ethz.idsc.owl.rrts.core.Transition;
import ch.ethz.idsc.owl.rrts.core.TransitionSpace;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Norm;

public class RnTransitionSpace implements TransitionSpace, Serializable {
  public static final TransitionSpace INSTANCE = new RnTransitionSpace();

  private RnTransitionSpace() {
    // ---
  }

  @Override // from TransitionSpace
  public Transition connect(Tensor start, Tensor end) {
    return new RnTransition(start, end);
  }

  @Override // from TransitionSpace
  public Scalar distance(Tensor start, Tensor end) {
    return Norm._2.between(start, end);
  }
}
