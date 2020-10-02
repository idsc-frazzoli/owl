// code by jph
package ch.ethz.idsc.owl.math.state;

import java.util.Collections;

import ch.ethz.idsc.owl.math.AssertFail;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class TrajectoryWrapTest extends TestCase {
  public void testSimple() {
    StateTime stateTime = new StateTime(Tensors.vector(1, 2, 3), RealScalar.of(4));
    TrajectorySample trajectorySample = new TrajectorySample(stateTime, null);
    TrajectoryWrap trajectoryWrap = TrajectoryWrap.of(Collections.singletonList(trajectorySample));
    assertTrue(trajectoryWrap.isRelevant(RealScalar.of(3)));
    assertFalse(trajectoryWrap.isDefined(RealScalar.of(4)));
    AssertFail.of(()->
      trajectoryWrap.getControl(RealScalar.of(3)));
    AssertFail.of(()->
      trajectoryWrap.getSample(RealScalar.of(3)));
    AssertFail.of(()->
      trajectoryWrap.getSample(RealScalar.of(4)));
  }
}
