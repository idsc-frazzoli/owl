// code by jph
package ch.ethz.idsc.owl.bot.se2.pid;

import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.sophus.group.Se2CoveringIntegrator;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;
import junit.framework.TestCase;

public class ConvergenceTest extends TestCase {
  private Scalar maxTurningRate = Pi.HALF;
  private Clip turningRate = Clips.interval(maxTurningRate.negate(), maxTurningRate);
  private PIDGains pidGains = new PIDGains(Quantity.of(10, "m^-1"), RealScalar.ZERO, Quantity.of(1, "s*m^-1"));
  private PIDTrajectory pidTrajectory = null;
  private Tensor pose = Tensors.of(RealScalar.ONE, RealScalar.ZERO, RealScalar.ZERO);

  public void testSimple() {
    Tensor traj = Tensors.vector(i -> Tensors.of(RealScalar.of(1), RealScalar.of(i / 10), Pi.HALF), 2000);
    for (int index = 0; index < 100; ++index) {
      StateTime stateTime = new StateTime(pose, RealScalar.of(index));
      PIDTrajectory _pidTrajectory = new PIDTrajectory(index, pidTrajectory, pidGains, traj, stateTime);
      pidTrajectory = _pidTrajectory;
      Scalar angleOut = pidTrajectory.angleOut();
      if (turningRate.isOutside(angleOut)) {
        if (Scalars.lessEquals(turningRate.max().abs(), angleOut)) {
          angleOut = Pi.HALF;
        } else if (Scalars.lessEquals(angleOut, turningRate.min())) {
          angleOut = Pi.HALF.negate();
        }
      }
      pose = Se2CoveringIntegrator.INSTANCE. //
          spin(pose, Tensors.of(RealScalar.of(0), RealScalar.of(0.1), angleOut));
      stateTime = new StateTime(pose, stateTime.time().add(RealScalar.of(.01)));
      System.out.println(pose);
      System.out.println("angle out " + angleOut);
      System.out.println(pidTrajectory.getProp());
      System.out.println(pidTrajectory.getDeriv());
      System.out.println("------------------_");
    }
  }

  public void testPoseAngle() {
    Tensor initialPose = Tensors.of(RealScalar.ZERO, RealScalar.ZERO, Pi.HALF);
    Scalar angle = Pi.HALF;
    Tensor pose = Se2CoveringIntegrator.INSTANCE. //
        spin(initialPose, Tensors.of(RealScalar.of(0), RealScalar.of(0), angle));
    // FIXME JPH/MAX should this return the same pose as the initial pose? Why is angle of pose = pi??
    // shouldnt be pi.half as velocities are null
    // System.out.println(pose);
    // assertTrue(Chop._03.close(pose, initialPose));
  }
}
