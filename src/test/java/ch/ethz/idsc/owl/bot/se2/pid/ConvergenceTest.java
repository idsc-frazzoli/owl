// code by jph
package ch.ethz.idsc.owl.bot.se2.pid;

import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.sophus.group.Se2CoveringIntegrator;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.qty.Degree;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class ConvergenceTest extends TestCase {
  private Scalar maxTurningRate = Degree.of(50);
  private PIDGains pidGains = new PIDGains(Quantity.of(5, "m^-1"), Quantity.of(1, "s*m^-1"));
  private PIDTrajectory pidTrajectory = null;
  private Tensor pose = Tensors.fromString("{6.2,4.2,1}");

  public void testSimple() {
    Tensor traj = //
        Tensors.vector(i -> Tensors.of(RealScalar.of(1), RealScalar.of(i), Pi.HALF), 20);
    for (int index = 0; index < 100; ++index) {
      StateTime stateTime = new StateTime(pose, RealScalar.of(index));
      PIDTrajectory _pidTrajectory = new PIDTrajectory(index, pidTrajectory, pidGains, traj, stateTime);
      pidTrajectory = _pidTrajectory;
      Scalar angleOut = pidTrajectory.angleOut();
      pose = Se2CoveringIntegrator.INSTANCE. //
          spin(pose, Tensors.of(RealScalar.of(.10), RealScalar.of(0), angleOut));
      stateTime = new StateTime(pose, stateTime.time().add(RealScalar.of(.1)));
      System.out.println(angleOut);
    }
  }
}
