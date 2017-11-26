// code by jph
package ch.ethz.idsc.owl.bot.se2;

import java.util.Arrays;

import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dimensions;
import junit.framework.TestCase;

public class Se2PointsVsRegionsTest extends TestCase {
  public void testSimple() {
    Se2PointsVsRegion se2PointsVsRegion = Se2PointsVsRegions.line(Tensors.vector(-2, 1, 0, 5), null);
    assertEquals(Dimensions.of(se2PointsVsRegion.points()), Arrays.asList(4, 2)); // affine...
  }
}
