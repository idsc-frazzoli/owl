// code by jph
package ch.ethz.idsc.owl.math.region;

import java.util.Arrays;

import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
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

  public void testCorner() {
    Tensor image = ResourceData.of("/io/delta_free.png");
    assertEquals(Dimensions.of(image), Arrays.asList(128, 179));
    Region<Tensor> region = new ImageRegion(image, Tensors.vector(179, 128), false);
    for (int x = 0; x < 179; ++x)
      for (int y = 0; y < 128; ++y) {
        assertEquals( //
            Scalars.nonZero(image.Get(y, x)), //
            region.isMember(Tensors.vector(x, 127 - y, 7)));
      }
  }

  public void testOutsideFalse() {
    Tensor image = ResourceData.of("/io/delta_free.png");
    assertEquals(Dimensions.of(image), Arrays.asList(128, 179));
    Region<Tensor> region = new ImageRegion(image, Tensors.vector(179, 128), false);
    assertFalse(region.isMember(Tensors.vector(-100, -2000, 3)));
  }

  public void testOutsideTrue() {
    Tensor image = ResourceData.of("/io/delta_free.png");
    assertEquals(Dimensions.of(image), Arrays.asList(128, 179));
    Tensor range = Tensors.vector(179, 128);
    ImageRegion region = new ImageRegion(image, range, true);
    assertEquals(region.range(), range);
    assertTrue(region.isMember(Tensors.vector(-100, -2000, 3)));
    assertEquals(region.origin(), Array.zeros(2));
  }
}
