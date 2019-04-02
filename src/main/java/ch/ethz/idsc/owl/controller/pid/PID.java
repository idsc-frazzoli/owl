// code by mcp
package ch.ethz.idsc.owl.controller.pid;

import java.util.Objects;

import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Norm;

public class PID {
  Scalar angleOut;
  Scalar errorPose;
  Scalar time;
  Scalar prop;
  Scalar deriv = RealScalar.ZERO;

  public PID(PID _pid, Tensor traj, StateTime stateTime) {
    time = stateTime.time();
    Tensor stateXYphi = stateTime.state();
    Tensor closest = traj.get(PIDCurveHelper.closest(traj, stateXYphi));
    errorPose = Norm._2.between(stateXYphi, closest);
    prop = PIDGains.Kp.multiply(errorPose);
    if (Objects.nonNull(_pid)) {
      Scalar dt = time.subtract(_pid.time);
      deriv = PIDGains.Kd.multiply((errorPose.subtract(_pid.errorPose)).divide(dt));
    }
    angleOut = prop.add(deriv);
  }

  public Scalar angleOut() {
    return angleOut;
  }
}
