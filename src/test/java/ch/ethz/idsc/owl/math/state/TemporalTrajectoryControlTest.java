// code by jph
package ch.ethz.idsc.owl.math.state;

import java.util.List;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class TemporalTrajectoryControlTest extends TestCase {
  public void testFallback() {
    TrajectoryControl tc = new TemporalTrajectoryControl(Tensors.vector(1, 2));
    Tensor control = tc.control(new StateTime(Tensors.vector(3, 4), RealScalar.of(2)), RealScalar.of(3));
    assertEquals(control, Tensors.vector(1, 2));
  }

  public void testFutureTrajectoryUntil() {
    TrajectoryControl tc = new TemporalTrajectoryControl(Tensors.vector(1, 2));
    List<TrajectorySample> trajectory = //
        tc.getFutureTrajectoryUntil(new StateTime(Tensors.vector(3, 4), RealScalar.of(2)), RealScalar.of(3));
    assertEquals(trajectory.size(), 1);
    assertEquals(trajectory.get(0).stateTime().time(), RealScalar.of(5));
  }

  public void testSetTrajectoryNull() {
    TrajectoryControl tc = new TemporalTrajectoryControl(Tensors.vector(1, 2));
    tc.setTrajectory(null);
  }
}
