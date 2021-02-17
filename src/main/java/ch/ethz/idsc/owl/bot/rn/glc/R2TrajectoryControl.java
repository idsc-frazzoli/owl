// code by jph
package ch.ethz.idsc.owl.bot.rn.glc;

import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.owl.ani.adapter.StateTrajectoryControl;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.nrm.Vector2Norm;
import ch.ethz.idsc.tensor.nrm.Vector2NormSquared;

public class R2TrajectoryControl extends StateTrajectoryControl {
  private static final Scalar THRESHOLD = RealScalar.of(0.2);

  @Override
  protected Optional<Tensor> customControl(StateTime tail, List<TrajectorySample> trailAhead) {
    Tensor state = tail.state();
    for (TrajectorySample trajectorySample : trailAhead) {
      Tensor diff = trajectorySample.stateTime().state().subtract(state);
      if (Scalars.lessThan(THRESHOLD, Vector2Norm.of(diff)))
        return Optional.of(Vector2Norm.NORMALIZE.apply(diff));
    }
    // System.out.println("fail custom control");
    return Optional.empty();
  }

  @Override // from StateTrajectoryControl
  protected Scalar pseudoDistance(Tensor x, Tensor y) {
    return Vector2NormSquared.between(x, y);
  }
}
