// code by gjoel
package ch.ethz.idsc.owl.bot.rn.rrts;

import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public class RnRrtsFlow {
  public static Tensor uBetween(StateTime orig, StateTime dest) {
    Tensor direction = dest.state().subtract(orig.state());
    Scalar delta = dest.time().subtract(orig.time());
    return direction.divide(delta);
  }
}
