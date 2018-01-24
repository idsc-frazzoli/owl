package ch.ethz.idsc.owl.bot.psu;

import ch.ethz.idsc.owl.math.state.AbstractTrajectoryControl;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class PsuTrajectoryControl extends AbstractTrajectoryControl {
  public static final Tensor FALLBACK_CONTROL = Tensors.vectorDouble(0).unmodifiable();

  public PsuTrajectoryControl() {
    super(StateTime::state);
  }

  @Override
  protected Scalar distance(Tensor x, Tensor y) {
    return PsuWrap.INSTANCE.distance(x, y);
  }

  @Override
  protected Tensor fallbackControl() {
    return FALLBACK_CONTROL;
  }
}
