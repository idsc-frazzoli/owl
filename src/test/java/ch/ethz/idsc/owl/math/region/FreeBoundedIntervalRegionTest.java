// code by jph
package ch.ethz.idsc.owl.math.region;

import ch.ethz.idsc.owl.glc.adapter.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectoryRegionQuery;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class FreeBoundedIntervalRegionTest extends TestCase {
  public void testSimple() {
    FreeBoundedIntervalRegion bir = new FreeBoundedIntervalRegion(0, RealScalar.of(10), RealScalar.of(20));
    assertEquals(bir.apply(Tensors.vector(+5)), RealScalar.of(-5));
    assertEquals(bir.apply(Tensors.vector(10)), RealScalar.of(+0));
    assertEquals(bir.apply(Tensors.vector(15)), RealScalar.of(+5));
    assertEquals(bir.apply(Tensors.vector(20)), RealScalar.of(+0));
    assertEquals(bir.apply(Tensors.vector(25)), RealScalar.of(-5));
  }

  public void testTrajectoryMember() {
    FreeBoundedIntervalRegion bir = new FreeBoundedIntervalRegion(0, RealScalar.of(10), RealScalar.of(20));
    TrajectoryRegionQuery trq = SimpleTrajectoryRegionQuery.timeInvariant(bir);
    assertFalse(trq.isMember(new StateTime(Tensors.vector(15), RealScalar.ZERO)));
    assertTrue(trq.isMember(new StateTime(Tensors.vector(5), RealScalar.ZERO)));
  }

  public void testFail() {
    try {
      new FreeBoundedIntervalRegion(0, RealScalar.of(10), RealScalar.of(10));
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
  }
}
