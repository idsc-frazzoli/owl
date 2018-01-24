// code by jph
package ch.ethz.idsc.owl.bot.rn.glc;

import ch.ethz.idsc.owl.math.state.AbstractTrajectoryControl;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class R2xTTrajectoryControl extends AbstractTrajectoryControl {
  private static final Tensor FALLBACK_CONTROL = Tensors.vectorDouble(0, 0).unmodifiable();
  // TODO not sure what is a good approach here:
  private static final Tensor WEIGHT = Tensors.vector(1.0, 1.0, 0.2);

  public R2xTTrajectoryControl() {
    super(StateTime::joined);
  }

  @Override
  protected Tensor fallbackControl() {
    return FALLBACK_CONTROL;
  }

  @Override
  protected Scalar distance(Tensor x, Tensor y) {
    Tensor d = x.subtract(y);
    return d.pmul(WEIGHT).dot(d).Get();
  }
}
