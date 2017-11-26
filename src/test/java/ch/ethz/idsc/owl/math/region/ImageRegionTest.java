// code by jph
package ch.ethz.idsc.owl.math.region;

import java.util.Arrays;

import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.io.ResourceData;
import junit.framework.TestCase;

public class ImageRegionTest extends TestCase {
  public void testSimple() {
    Tensor image = ResourceData.of("/io/delta_free.png");
    assertEquals(Dimensions.of(image), Arrays.asList(128, 179));
    Region<Tensor> region = new ImageRegion(image, Tensors.vector(179, 128), false);
    for (int x = 0; x < 179; ++x)
      for (int y = 0; y < 128; ++y) {
        assertEquals( //
            Scalars.nonZero(image.Get(y, x)), //
            region.isMember(Tensors.vector(x, 127 - y)));
      }
  }
}
