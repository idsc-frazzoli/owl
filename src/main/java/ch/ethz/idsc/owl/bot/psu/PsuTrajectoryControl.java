// code by jph
package ch.ethz.idsc.owl.bot.psu;

import ch.ethz.idsc.owl.math.state.SpacialTrajectoryControl;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;

public class PsuTrajectoryControl extends SpacialTrajectoryControl {
  public static final Tensor FALLBACK_CONTROL = Array.zeros(2);

  public PsuTrajectoryControl() {
    super(FALLBACK_CONTROL);
  }

  @Override
  protected Scalar distance(Tensor x, Tensor y) {
    return PsuWrap.INSTANCE.distance(x, y);
  }
}
