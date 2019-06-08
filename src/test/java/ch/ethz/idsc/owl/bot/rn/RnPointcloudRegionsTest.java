// code by jph
package ch.ethz.idsc.owl.bot.rn;

import java.util.Arrays;

import ch.ethz.idsc.owl.bot.r2.ImageRegions;
import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dimensions;
import junit.framework.TestCase;

public class RnPointcloudRegionsTest extends TestCase {
  public void testSimple2D() {
    Region<Tensor> region = RnPointcloudRegions.createRandomRegion(1, Tensors.vector(10, 10), Tensors.vector(1, 1), RealScalar.of(1.5));
    assertTrue(region.isMember(Tensors.vector(10.5, 10.5)));
    assertTrue(region.isMember(Tensors.vector(10, 10)));
    assertFalse(region.isMember(Tensors.vector(8, 8)));
    assertFalse(region.isMember(Tensors.vector(13, 13)));
  }

  public void testPointsMatrix() {
    ImageRegion imageRegion = ImageRegions.loadFromRepository( //
        "/io/track0_100.png", Tensors.vector(10, 10), false);
    Tensor points = RnPointcloudRegions.points(imageRegion);
    assertEquals(Dimensions.of(points), Arrays.asList(1429, 2));
  }
}
