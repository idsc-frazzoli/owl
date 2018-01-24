// code by jph
package ch.ethz.idsc.owl.bot.rn.glc;

import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.owl.math.state.EuclideanTrajectoryControl;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Normalize;
import ch.ethz.idsc.tensor.red.Norm;

public class R2TrajectoryControl extends EuclideanTrajectoryControl {
  public R2TrajectoryControl() {
    super(Array.zeros(2));
  }

  @Override
  protected Optional<Tensor> customControl(StateTime tail, List<TrajectorySample> trailAhead) {
    Tensor state = tail.state();
    for (TrajectorySample trajectorySample : trailAhead) {
      Tensor diff = trajectorySample.stateTime().state().subtract(state);
      if (Scalars.lessThan(RealScalar.of(0.2), Norm._2.ofVector(diff))) // magic const
        return Optional.of(Normalize.of(diff));
    }
    // System.out.println("fail custom control");
    return Optional.empty();
  }
}
