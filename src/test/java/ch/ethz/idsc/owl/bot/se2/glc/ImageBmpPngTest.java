// code by jph
package ch.ethz.idsc.owl.bot.se2.glc;

import java.util.Objects;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.io.ResourceData;
import junit.framework.TestCase;

public class ImageBmpPngTest extends TestCase {
  public void testSimple() {
    Tensor bmp = ResourceData.of("/map/scenarios/S1_ped_obs_legal.BMP");
    Tensor png = ResourceData.of("/map/scenarios/s1/ped_obs_legal.png");
    if (Objects.nonNull(bmp))
      assertEquals(Dimensions.of(bmp).size(), 2);
    if (Objects.nonNull(png))
      assertEquals(Dimensions.of(png).size(), 2);
  }
}
