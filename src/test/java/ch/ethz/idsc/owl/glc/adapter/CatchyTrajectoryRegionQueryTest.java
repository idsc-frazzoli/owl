// code by jph
package ch.ethz.idsc.owl.glc.adapter;

import java.util.Collections;
import java.util.Optional;

import ch.ethz.idsc.owl.math.region.EllipsoidRegion;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.StateTimeCollector;
import ch.ethz.idsc.owl.math.state.TrajectoryRegionQuery;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class CatchyTrajectoryRegionQueryTest extends TestCase {
  public void testSimple() {
    Region<Tensor> region = new EllipsoidRegion(Tensors.vector(1, 2), Tensors.vector(3, 4));
    TrajectoryRegionQuery trq = CatchyTrajectoryRegionQuery.timeDependent(region);
    StateTime stateTime = new StateTime(Tensors.vector(1), RealScalar.ZERO);
    Optional<StateTime> optional = trq.firstMember(Collections.singletonList(stateTime));
    assertTrue(optional.isPresent());
    assertEquals(optional.get(), stateTime);
  }

  public void testMembers1d() {
    Region<Tensor> region = new EllipsoidRegion(Tensors.vector(1, 2), Tensors.vector(3, 4));
    TrajectoryRegionQuery trq = CatchyTrajectoryRegionQuery.timeDependent(region);
    StateTimeCollector stc = (StateTimeCollector) trq;
    assertTrue(stc.getMembers().isEmpty());
    StateTime stateTime = new StateTime(Tensors.vector(1), RealScalar.ZERO);
    Optional<StateTime> optional = trq.firstMember(Collections.singletonList(stateTime));
    assertTrue(optional.isPresent());
    assertEquals(optional.get(), stateTime);
    assertFalse(stc.getMembers().isEmpty());
  }

  public void testMembers2d() {
    Region<Tensor> region = new EllipsoidRegion(Tensors.vector(1, 2, 3), Tensors.vector(3, 4, 8));
    TrajectoryRegionQuery trq = CatchyTrajectoryRegionQuery.timeDependent(region);
    StateTimeCollector stc = (StateTimeCollector) trq;
    assertTrue(stc.getMembers().isEmpty());
    StateTime stateTime = new StateTime(Tensors.vector(1, 2), RealScalar.ZERO);
    Optional<StateTime> optional = trq.firstMember(Collections.singletonList(stateTime));
    assertTrue(optional.isPresent());
    assertEquals(optional.get(), stateTime);
    assertFalse(stc.getMembers().isEmpty());
  }
}
