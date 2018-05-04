// code by jph
package ch.ethz.idsc.owl.bot.se2;

import ch.ethz.idsc.owl.math.region.EllipsoidRegion;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class Se2PointsVsRegionTest extends TestCase {
  public void testSimple() {
    Tensor center = Tensors.vector(1, 2);
    Region<Tensor> region = new EllipsoidRegion(center, Tensors.vector(.1, .1));
    Tensor xya = Tensors.vector(4, 5, 6);
    Tensor point = Tensors.vector(7, 8);
    Region<Tensor> region2 = new Se2PointsVsRegion(Tensors.of(point), region);
    assertFalse(region2.isMember(xya));
  }
}
