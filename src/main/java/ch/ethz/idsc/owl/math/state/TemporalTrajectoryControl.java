// code by jph
package ch.ethz.idsc.owl.math.state;

import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Last;

public abstract class TemporalTrajectoryControl extends AbstractTrajectoryControl {
  public TemporalTrajectoryControl() {
    super(StateTime::joined);
  }

  @Override
  protected Scalar distance(Tensor x, Tensor y) {
    Scalar traj_time = Last.of(x).Get();
    Scalar state_time = Last.of(y).Get();
    return Scalars.lessEquals(traj_time, state_time) //
        ? state_time.subtract(traj_time)
        : DoubleScalar.POSITIVE_INFINITY;
  }
}
