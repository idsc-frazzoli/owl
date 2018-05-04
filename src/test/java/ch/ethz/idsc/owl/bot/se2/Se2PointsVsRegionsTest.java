// code by jph
package ch.ethz.idsc.owl.bot.se2;

import ch.ethz.idsc.owl.math.region.EllipsoidRegion;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.region.Regions;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.red.ScalarSummaryStatistics;
import junit.framework.TestCase;

public class Se2PointsVsRegionsTest extends TestCase {
  public void testSimple() {
    Region<Tensor> region = Se2PointsVsRegions.line(Tensors.vector(-2, 1, 0, 5), Regions.emptyRegion());
    assertFalse(region.isMember(Tensors.vector(1, 2, 3, 4))); // interpretation as xya
  }

  public void testFail() {
    try {
      Se2PointsVsRegions.line(Tensors.vector(-2, 1, 0, 5), null);
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
  }

  public void testFootprint() {
    Tensor SHAPE = ResourceData.of("/demo/gokart/footprint.csv");
    ScalarSummaryStatistics scalarSummaryStatistics = //
        SHAPE.stream().map(tensor -> tensor.Get(0)).collect(ScalarSummaryStatistics.collector());
    Tensor x_coords = Subdivide.of(scalarSummaryStatistics.getMin(), scalarSummaryStatistics.getMax(), 3);
    Tensor center = Tensors.vector(2, 0);
    Region<Tensor> region = new EllipsoidRegion(center, Tensors.vector(1, 1));
    Region<Tensor> query = Se2PointsVsRegions.line(x_coords, region);
    assertTrue(query.isMember(Tensors.vector(0, 0, 0)));
    assertFalse(query.isMember(Tensors.vector(0, 0, 3.14 / 2)));
    assertFalse(query.isMember(Tensors.vector(0, 0, 3.14)));
    assertFalse(query.isMember(Tensors.vector(0, 0, 3 * 3.14 / 2)));
  }
}
