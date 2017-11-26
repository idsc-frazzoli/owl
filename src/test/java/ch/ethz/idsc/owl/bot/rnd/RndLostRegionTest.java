// code by jph
package ch.ethz.idsc.owl.bot.rnd;

import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Clip;
import junit.framework.TestCase;

public class RndLostRegionTest extends TestCase {
  public void testSimple() {
    Region<Tensor> region = new RndLostRegion(Clip.function(3, 10));
    assertTrue(region.isMember(Tensors.vector(1, 1, 100, 0)));
    assertFalse(region.isMember(Tensors.vector(1, 1, 1, 5)));
    assertTrue(region.isMember(Tensors.vector(1, 1, 1, 2)));
    assertTrue(region.isMember(Tensors.vector(1, 1, 2, 2)));
  }
}
