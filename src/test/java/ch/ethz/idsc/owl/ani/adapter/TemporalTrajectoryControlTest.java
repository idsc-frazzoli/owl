// code by jph
package ch.ethz.idsc.owl.ani.adapter;

import java.util.List;

import ch.ethz.idsc.owl.ani.api.EntityControl;
import ch.ethz.idsc.owl.ani.api.TrajectoryControl;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class TemporalTrajectoryControlTest extends TestCase {
  public void testFallback() {
    EntityControl entityControl = new FallbackControl(Tensors.vector(1, 2));
    Tensor control = entityControl.control(new StateTime(Tensors.vector(3, 4), RealScalar.of(2)), RealScalar.of(3)).get();
    assertEquals(control, Tensors.vector(1, 2));
  }

  public void testFutureTrajectoryUntil() {
    TrajectoryControl trajectoryControl = TemporalTrajectoryControl.createInstance();
    List<TrajectorySample> trajectory = //
        trajectoryControl.getFutureTrajectoryUntil(new StateTime(Tensors.vector(3, 4), RealScalar.of(2)), RealScalar.of(3));
    assertEquals(trajectory.size(), 1);
    assertEquals(trajectory.get(0).stateTime().time(), RealScalar.of(5));
  }

  public void testSetTrajectoryNull() {
    TrajectoryControl trajectoryControl = TemporalTrajectoryControl.createInstance();
    trajectoryControl.trajectory(null);
  }
}
