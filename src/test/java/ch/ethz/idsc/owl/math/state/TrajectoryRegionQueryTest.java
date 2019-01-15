// code by jph
package ch.ethz.idsc.owl.math.state;

import java.util.ArrayList;
import java.util.List;

import ch.ethz.idsc.owl.glc.adapter.CatchyTrajectoryRegionQuery;
import ch.ethz.idsc.owl.math.region.EllipsoidRegion;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class TrajectoryRegionQueryTest extends TestCase {
  public void testDisjoint() {
    TrajectoryRegionQuery goalQuery = //
        CatchyTrajectoryRegionQuery.timeInvariant( //
            new EllipsoidRegion(Tensors.vector(10, 5), Tensors.vector(1, 1)));
    List<StateTime> trajectory = new ArrayList<>();
    trajectory.add(new StateTime(Tensors.vector(0, 5), RealScalar.ZERO));
    trajectory.add(new StateTime(Tensors.vector(5, 5), RealScalar.ZERO));
    assertFalse(goalQuery.firstMember(trajectory).isPresent());
    assertTrue(!goalQuery.firstMember(trajectory).isPresent());
    // ---
    StateTime term = new StateTime(Tensors.vector(10, 5), RealScalar.ZERO);
    trajectory.add(term);
    assertTrue(goalQuery.firstMember(trajectory).isPresent());
    assertEquals(goalQuery.firstMember(trajectory).get(), term);
    assertFalse(!goalQuery.firstMember(trajectory).isPresent());
  }
}
