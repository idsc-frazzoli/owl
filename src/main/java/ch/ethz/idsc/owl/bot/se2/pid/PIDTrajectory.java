// code by mcp
package ch.ethz.idsc.owl.bot.se2.pid;

import java.util.Objects;

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
  private Scalar prop;
  private Scalar deriv = RealScalar.ZERO;

  public PIDTrajectory(PIDTrajectory _pid, PIDGains pidGains, Tensor traj, StateTime stateTime) {
    time = Quantity.of(stateTime.time(), "s");
    Tensor trajInMeter = new Se2CurveConverter().toSI(traj);
    Tensor stateXYphi = new Se2PoseConverter().toSI(stateTime.state());
    Tensor closest = trajInMeter.get(Se2CurveHelper.closest(trajInMeter, stateXYphi));
    errorPose = Se2ParametricDistance.of(stateXYphi, closest);
    prop = pidGains.Kp.multiply(errorPose);
    if (Objects.nonNull(_pid)) {
      Scalar dt = time.subtract(_pid.time);
      deriv = pidGains.Kd.multiply((errorPose.subtract(_pid.errorPose)).divide(dt));
    }
    angleOut = prop.add(deriv);
  }

  public Scalar angleOut() {
    return angleOut;
  }
}
