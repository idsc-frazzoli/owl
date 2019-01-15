// code by jph
package ch.ethz.idsc.owl.bot.psu;

import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.owl.ani.adapter.StateTrajectoryControl;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/* package */ final class PsuTrajectoryControl extends StateTrajectoryControl {
  @Override // from StateTrajectoryControl
  protected Scalar pseudoDistance(Tensor x, Tensor y) {
    return PsuMetric.INSTANCE.distance(x, y);
  }

  @Override // from StateTrajectoryControl
  protected Optional<Tensor> customControl(StateTime tail, List<TrajectorySample> trailAhead) {
    return Optional.empty();
  }
}
