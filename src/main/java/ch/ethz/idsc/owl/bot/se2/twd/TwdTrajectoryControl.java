// code by jph
package ch.ethz.idsc.owl.bot.se2.twd;

import ch.ethz.idsc.owl.bot.se2.Se2Wrap;
import ch.ethz.idsc.owl.math.state.StateTrajectoryControl;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Norm;

public class TwdTrajectoryControl extends StateTrajectoryControl {
  @Override
  protected Scalar distance(Tensor x, Tensor y) {
    return Norm._2.ofVector(Se2Wrap.INSTANCE.difference(x, y));
  }
}
