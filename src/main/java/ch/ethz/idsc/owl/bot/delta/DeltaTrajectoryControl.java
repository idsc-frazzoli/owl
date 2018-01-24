// code by jph
package ch.ethz.idsc.owl.bot.delta;

import ch.ethz.idsc.owl.math.state.SpacialTrajectoryControl;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.red.Norm2Squared;

public class DeltaTrajectoryControl extends SpacialTrajectoryControl {
  public DeltaTrajectoryControl() {
    super(Array.zeros(2));
  }

  @Override
  protected Scalar distance(Tensor x, Tensor y) {
    return Norm2Squared.between(x, y);
  }
}
