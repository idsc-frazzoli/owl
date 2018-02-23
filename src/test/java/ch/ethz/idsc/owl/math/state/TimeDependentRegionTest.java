// code by jph
package ch.ethz.idsc.owl.math.state;

import ch.ethz.idsc.owl.math.region.EllipsoidRegion;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class TimeDependentRegionTest extends TestCase {
  public void testSimple() {
    Region<StateTime> region = //
        new TimeDependentRegion(new EllipsoidRegion(Tensors.vector(1, 2), Tensors.vector(3, 4)));
    assertTrue(region.isMember(new StateTime(Tensors.vector(1), RealScalar.of(2))));
    assertFalse(region.isMember(new StateTime(Tensors.vector(1), RealScalar.of(7))));
  }
}
