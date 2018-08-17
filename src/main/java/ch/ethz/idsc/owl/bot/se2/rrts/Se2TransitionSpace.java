// code by jph
package ch.ethz.idsc.owl.bot.se2.rrts;

import java.io.Serializable;

import ch.ethz.idsc.owl.rrts.core.TransitionSpace;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public class Se2TransitionSpace implements TransitionSpace, Serializable {
  private final Scalar radius;

  public Se2TransitionSpace(Scalar radius) {
    this.radius = radius;
  }

  @Override // from TransitionSpace
  public Se2Transition connect(Tensor start, Tensor end) {
    return new Se2Transition(start, end, radius);
  }
}
