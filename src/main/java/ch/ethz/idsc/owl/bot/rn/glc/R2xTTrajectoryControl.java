// code by jph
package ch.ethz.idsc.owl.bot.rn.glc;

import java.util.List;

import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TemporalTrajectoryControl;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;

public class R2xTTrajectoryControl extends TemporalTrajectoryControl {
  // TODO not sure what is a good approach here:
  private static final Tensor WEIGHT = Tensors.vector(1.0, 1.0, 0.2);

  public R2xTTrajectoryControl() {
    super(Array.zeros(2));
  }

  @Override
  public List<TrajectorySample> getFutureTrajectoryUntil(StateTime tail, Scalar delay) {
    // TODO Auto-generated method stub
    return null;
  }
}
