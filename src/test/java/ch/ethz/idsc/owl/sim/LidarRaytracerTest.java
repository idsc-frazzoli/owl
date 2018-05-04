// code by jph
package ch.ethz.idsc.owl.sim;

import java.util.Arrays;

import ch.ethz.idsc.owl.math.state.EmptyTrajectoryRegionQuery;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.alg.Subdivide;
import junit.framework.TestCase;

public class LidarRaytracerTest extends TestCase {
  public void testSimple() {
    LidarRaytracer lidarRaytracer = new LidarRaytracer(Subdivide.of(-1, 1, 10), Subdivide.of(0., 5., 30));
    StateTime stateTime = new StateTime(Tensors.vector(1, 2, 3), RealScalar.ZERO);
    Tensor scan = lidarRaytracer.scan(stateTime, EmptyTrajectoryRegionQuery.INSTANCE);
    assertEquals(scan, Array.of(l -> RealScalar.of(5), 11));
    Tensor poly = lidarRaytracer.toPoints(scan);
    assertEquals(Dimensions.of(poly), Arrays.asList(11, 2));
  }
}
