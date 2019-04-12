// code by mcp
package ch.ethz.idsc.owl.bot.se2.pid;

import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.sophus.group.Se2ParametricDistance;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.qty.Quantity;

public class PIDTrajectory {
  private Scalar angleOut;
  private Scalar errorPose;
  private Scalar time;
  private Scalar deriv = RealScalar.ZERO;

  public PIDTrajectory(int pidIndex, PIDTrajectory previousPID, PIDGains pidGains, Tensor traj, StateTime stateTime) {
    this.time = Quantity.of(stateTime.time(), "s");
    Tensor trajInMeter = new Se2CurveConverter().toSI(traj);
    Tensor stateXYphi = new Se2PoseConverter().toSI(stateTime.state());
    Tensor closest = trajInMeter.get(Se2CurveHelper.closest(trajInMeter, stateXYphi));
    this.errorPose = Se2ParametricDistance.of(stateXYphi, closest);
    Scalar prop = pidGains.Kp.multiply(errorPose);
    if (pidIndex>1) {
      Scalar dt = time.subtract(previousPID.time);
      deriv = pidGains.Kd.multiply((errorPose.subtract(previousPID.errorPose)).divide(dt));
    }
    angleOut = prop.add(deriv);
  }

  public Scalar angleOut() {
    return angleOut;
  }
}
