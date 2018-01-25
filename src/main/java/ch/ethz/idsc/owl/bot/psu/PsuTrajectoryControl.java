// code by jph
package ch.ethz.idsc.owl.bot.psu;

import ch.ethz.idsc.owl.math.state.StateTrajectoryControl;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public class PsuTrajectoryControl extends StateTrajectoryControl {
  @Override
  protected Scalar distance(Tensor x, Tensor y) {
    return PsuWrap.INSTANCE.distance(x, y);
  }
}
