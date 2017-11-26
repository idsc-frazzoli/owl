// code by jph
package ch.ethz.idsc.owl.bot.se2.glc;

import ch.ethz.idsc.owl.bot.se2.Se2Wrap;
import ch.ethz.idsc.owl.glc.adapter.Expand;
import ch.ethz.idsc.owl.glc.adapter.IdentityWrap;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Norm;
import junit.framework.TestCase;

public class Se2WrapDemoTest extends TestCase {
  public void testWrap() {
    TrajectoryPlanner trajectoryPlanner = Se2WrapDemo.createPlanner(new Se2Wrap(Tensors.vector(1, 1, 1)));
    int iters = Expand.maxSteps(trajectoryPlanner, 20);
    assertEquals(iters, 20);
    iters = Expand.maxSteps(trajectoryPlanner, 100);
    assertTrue(iters < 100);
    GlcNode glcNode = trajectoryPlanner.getBest().get();
    Scalar error = Norm._2.ofVector(glcNode.state().subtract(Tensors.vector(-0.2, 0, Math.PI * 2)));
    assertTrue(Scalars.lessThan(error, RealScalar.of(0.5)));
  }

  public void testIdentity() {
    TrajectoryPlanner trajectoryPlanner = Se2WrapDemo.createPlanner(IdentityWrap.INSTANCE);
    int iters = Expand.maxSteps(trajectoryPlanner, 1000);
    assertEquals(iters, 1000);
    iters = Expand.maxSteps(trajectoryPlanner, 1000);
    assertTrue(iters < 1000);
    GlcNode glcNode = trajectoryPlanner.getBest().get();
    Scalar error = Norm._2.ofVector(glcNode.state().subtract(Tensors.vector(-0.2, 0, 0)));
    assertTrue(Scalars.lessThan(error, RealScalar.of(0.5)));
  }
}
