// code by jph
package ch.ethz.idsc.owl.bot.se2;

import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.region.Regions;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class Se2PointsVsRegionsTest extends TestCase {
  public void testSimple() {
    Region<Tensor> region = Se2PointsVsRegions.line(Tensors.vector(-2, 1, 0, 5), Regions.emptyRegion());
    assertFalse(region.isMember(Tensors.vector(1, 2, 3, 4))); // interpretation as xya
    // assertEquals(Dimensions.of(se2PointsVsRegion.points()), Arrays.asList(4, 2)); // affine...
    // TODO test better
  }

  public void testFail() {
    try {
      Se2PointsVsRegions.line(Tensors.vector(-2, 1, 0, 5), null);
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
  }
}
