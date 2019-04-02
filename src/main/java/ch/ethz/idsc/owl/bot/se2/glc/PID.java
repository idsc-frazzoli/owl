// code by mcp
package ch.ethz.idsc.owl.bot.se2.glc;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Norm;

public class PID {
  Scalar angleOut = RealScalar.of(0.3);

  public PID(Tensor traj, Tensor state, Scalar lookAhead) {
    Tensor closest = traj.get(PIDCurveHelper.closest(traj, state));
    Scalar errorPose = Norm._2.between(state, closest);
    Scalar Kd = RealScalar.of(100 );
    angleOut = Kd.multiply(errorPose);
  }

  public Scalar angleOut() {
    // TODO Auto-generated method stub
    return angleOut;
  }
}
