// code by jph
package ch.ethz.idsc.owl.bot.se2.pid;

import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.sophus.group.Se2CoveringIntegrator;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
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
  private PIDGains pidGains = new PIDGains(Quantity.of(2, "m^-1"), Quantity.of(1, "s*m^-1"));
  private PIDTrajectory pidTrajectory = null;
  private Tensor pose = Tensors.of(RealScalar.ONE, RealScalar.ZERO, Pi.HALF);

  public void testSimple() {
    Tensor traj = //
        Tensors.vector(i -> Tensors.of(RealScalar.of(1), RealScalar.of(i), Pi.HALF), 20);
    for (int index = 0; index < 50; ++index) {
      StateTime stateTime = new StateTime(pose, RealScalar.of(index));
      PIDTrajectory _pidTrajectory = new PIDTrajectory(index, pidTrajectory, pidGains, traj, stateTime);
      pidTrajectory = _pidTrajectory;
      Scalar angleOut = pidTrajectory.angleOut();
      if (turningRate.isOutside(angleOut))
        angleOut = RealScalar.ZERO;
      pose = Se2CoveringIntegrator.INSTANCE. //
          spin(pose, Tensors.of(RealScalar.of(0), RealScalar.of(.10), angleOut)); //TODO MCP dont understand this
      stateTime = new StateTime(pose, stateTime.time().add(RealScalar.of(.1)));
      System.out.println(pose);
      System.out.println("angle out " + angleOut);
      System.out.println(pidTrajectory.getProp());
      System.out.println(pidTrajectory.getDeriv());
      System.out.println("------------------_");
    }
  }
}
