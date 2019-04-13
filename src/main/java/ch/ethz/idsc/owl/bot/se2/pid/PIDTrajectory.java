// code by mcp
package ch.ethz.idsc.owl.bot.se2.pid;

import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.sophus.group.Se2ParametricDistance;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public class PIDTrajectory {
  private final Scalar time;
  private final Scalar errorPose;
  private Scalar angleOut;
  private Scalar deriv = RealScalar.ZERO;

  public PIDTrajectory(int pidIndex, PIDTrajectory previousPID, PIDGains pidGains, Tensor traj, StateTime stateTime) {
    this.time = stateTime.time();
    Tensor stateXYphi = stateTime.state();
    Tensor closest = traj.get(Se2CurveHelper.closest(traj, stateXYphi));
    this.errorPose = Se2ParametricDistance.INSTANCE.distance(stateXYphi, closest);
    Scalar prop = pidGains.Kp.multiply(errorPose);
    if (pidIndex > 1) {
      Scalar dt = time.subtract(previousPID.time);
      deriv = pidGains.Kd.multiply((errorPose.subtract(previousPID.errorPose)).divide(dt));
    }
    angleOut = prop.add(deriv);
  }

  public Scalar angleOut() {
    return angleOut;
  }
}
