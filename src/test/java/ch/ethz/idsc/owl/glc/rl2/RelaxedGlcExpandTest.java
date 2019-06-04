// code by jph
package ch.ethz.idsc.owl.glc.rl2;

import java.util.Objects;

import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class RelaxedGlcExpandTest extends TestCase {
  public void testExpand() {
    RelaxedTrajectoryPlanner relaxedTrajectoryPlanner = TestHelper.createPlanner();
    Objects.requireNonNull(relaxedTrajectoryPlanner.getStateIntegrator());
    assertTrue(relaxedTrajectoryPlanner.getQueue().isEmpty());
    Objects.requireNonNull(relaxedTrajectoryPlanner.getBest());
    assertTrue(relaxedTrajectoryPlanner.getRelaxedDomainQueueMap().isEmpty());
    Tensor stateRoot = Tensors.vector(0, 0);
    relaxedTrajectoryPlanner.insertRoot(new StateTime(stateRoot, RealScalar.ZERO));
    RelaxedGlcExpand relaxedGlcExpand = new RelaxedGlcExpand(relaxedTrajectoryPlanner);
    assertFalse(relaxedGlcExpand.isOptimal());
    relaxedGlcExpand.findAny(100);
    assertTrue(relaxedGlcExpand.getExpandCount() < 100);
    assertFalse(relaxedGlcExpand.isOptimal());
    relaxedGlcExpand.untilOptimal(1000);
    assertTrue(relaxedGlcExpand.getExpandCount() < 200);
    assertTrue(relaxedGlcExpand.isOptimal());
  }
}
