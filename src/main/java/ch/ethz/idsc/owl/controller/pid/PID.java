// code by mcp
package ch.ethz.idsc.owl.controller.pid;

import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Norm;

public class PID {
  private Scalar angleOut;

  public PID(Tensor traj, StateTime stateTime) {
    Scalar currentTime = stateTime.time();
    Tensor stateXYphi = stateTime.state();
    Tensor closest = traj.get(PIDCurveHelper.closest(traj, stateXYphi));
    Scalar errorPose = Norm._2.between(stateXYphi, closest);
    angleOut = PIDGains.Kp.multiply(errorPose);
  }

  public Scalar angleOut() {
    return angleOut;
  }
}
