// code by mcp
package ch.ethz.idsc.owl.controller.pid;

import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.owl.ani.adapter.StateTrajectoryControl;
import ch.ethz.idsc.owl.bot.se2.Se2Wrap;
import ch.ethz.idsc.owl.bot.se2.glc.CarHelper;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Norm2Squared;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;

/** PID control */
@SuppressWarnings("serial")
public class PIDControl extends StateTrajectoryControl {
  private final Clip clip;
  private final Scalar lookAhead;
  PID pid = null;

  public PIDControl(Scalar lookAhead, Scalar maxTurningRate) {
    this.lookAhead = lookAhead;
    this.clip = Clips.interval(maxTurningRate.negate(), maxTurningRate);
  }

  @Override // from StateTrajectoryControl
  protected Scalar pseudoDistance(Tensor x, Tensor y) {
    return Norm2Squared.ofVector(Se2Wrap.INSTANCE.difference(x, y));
  }

  @Override // from AbstractEntity
  protected Optional<Tensor> customControl(StateTime tail, List<TrajectorySample> trailAhead) {
    Tensor u = trailAhead.get(0).getFlow().get().getU();
    Scalar speed = u.Get(0);
    Tensor state = tail.state();
    boolean requestControl = true; // TODO change
    Tensor traj = Tensor.of(trailAhead.stream() //
        .map(TrajectorySample::stateTime) //
        .map(StateTime::state));
    if (requestControl) {
      PID _pid = new PID(traj, state, lookAhead);
      Scalar ratePerMeter = _pid.angleOut();
      if (clip.isInside(ratePerMeter)) {
        pid = _pid;
        return Optional.of(CarHelper.singleton(speed, ratePerMeter).getU());
      }
    }
    pid = null;
    return Optional.empty();
  }
}
