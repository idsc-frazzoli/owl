// code by jph
package ch.ethz.idsc.owl.bot.se2.pid;

import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Pretty;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.qty.Degree;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class ConvergenceTest extends TestCase {
  private Scalar maxTurningRate = Degree.of(50);
  private PIDGains pidGains = new PIDGains(Quantity.of(3.5, "m^-1"), RealScalar.of(3));
  private PIDTrajectory pidTrajectory = null;
  private Tensor pose = Tensors.fromString("{6.2[m],4.2[m],1}");
  private StateTime stateTime = new StateTime(pose, RealScalar.ZERO);

  public void testSimple() {
    Tensor traj = //
        Tensors.vector(i -> Tensors.of(Quantity.of(1, "m"), Quantity.of(i, "m"), Pi.HALF), 20);
    System.out.println(Pretty.of(traj));
    for (int index = 0; index < 100; ++index) {
      PIDTrajectory _pidTrajectory = new PIDTrajectory(pidTrajectory, pidGains, traj, stateTime);
      pidTrajectory = _pidTrajectory;
      // TODO MCP THIS IS NOT WORKING
      // angleOut has unit [m]: 8.359229471391949[m]
      Scalar angleOut = pidTrajectory.angleOut();
      System.out.println(angleOut);
      break;
      // pose = Se2CoveringIntegrator.INSTANCE. //
      // spin(pose, Tensors.of(Quantity.of(.10, "m"), Quantity.of(0, "m"), angleOut));
      // stateTime = new StateTime(pose, stateTime.time().add(RealScalar.of(.1)));
    }
  }
}
