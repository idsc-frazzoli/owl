// code by jph
package ch.ethz.idsc.owl.math.state;

import java.util.Collections;
import java.util.Optional;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class TrajectoryWrapTest extends TestCase {
  public void testSimple() {
    StateTime stateTime = new StateTime(Tensors.vector(1, 2, 3), RealScalar.of(4));
    TrajectorySample trajectorySample = new TrajectorySample(stateTime, null);
    TrajectoryWrap trajectoryWrap = TrajectoryWrap.of(Collections.singletonList(trajectorySample));
    assertTrue(trajectoryWrap.hasRemaining(RealScalar.of(3)));
    assertFalse(trajectoryWrap.hasRemaining(RealScalar.of(4)));
    {
      Optional<Tensor> optional = trajectoryWrap.findControl(RealScalar.of(3));
      assertFalse(optional.isPresent());
    }
    {
      Optional<TrajectorySample> optional = trajectoryWrap.findTrajectorySample(RealScalar.of(3));
      assertTrue(optional.isPresent());
      assertEquals(optional.get().stateTime(), stateTime);
    }
    {
      Optional<TrajectorySample> optional = trajectoryWrap.findTrajectorySample(RealScalar.of(4));
      assertFalse(optional.isPresent());
    }
  }
}
