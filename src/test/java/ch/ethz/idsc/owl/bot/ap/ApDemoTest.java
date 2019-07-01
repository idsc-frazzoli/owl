// code by astoll 
package ch.ethz.idsc.owl.bot.ap;

import ch.ethz.idsc.owl.glc.adapter.GlcExpand;
import ch.ethz.idsc.owl.glc.std.StandardGlcTrajectoryPlanner;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import junit.framework.TestCase;

public class ApDemoTest extends TestCase {
  final static Tensor INITIAL_TENSOR = ApDemo.INITIAL;

  public void testApDemo() {
    StandardGlcTrajectoryPlanner standardTrajectoryPlanner = ApTrajectoryPlanner.apStandardTrajectoryPlanner();
    standardTrajectoryPlanner.insertRoot(new StateTime(INITIAL_TENSOR, RealScalar.ZERO));
    GlcExpand glcExpand = new GlcExpand(standardTrajectoryPlanner);
    glcExpand.findAny(15000);
    assertTrue(standardTrajectoryPlanner.getBest().isPresent());
  }
}
