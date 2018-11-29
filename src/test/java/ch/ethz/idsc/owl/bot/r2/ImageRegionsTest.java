// code by jph
package ch.ethz.idsc.owl.bot.r2;

import java.util.Arrays;

import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.alg.MatrixQ;
import ch.ethz.idsc.tensor.io.ResourceData;
import junit.framework.TestCase;

public class ImageRegionsTest extends TestCase {
  public void testSimple() {
    Tensor tensor = ResourceData.of("/io/track0_100.png");
    assertEquals(Dimensions.of(tensor), Arrays.asList(100, 100, 4));
    Tensor matrix = ImageRegions.grayscale(tensor);
    assertTrue(MatrixQ.of(matrix));
  }

  public void testDubendorf() {
    ImageRegion ir = ImageRegions.loadFromRepository( //
        "/dubilab/localization/20180122.png", Tensors.vector(10, 10), false);
    assertEquals(Dimensions.of(ir.image()), Arrays.asList(640, 640));
  }

  public void testGrayscale() {
    Tensor image = Tensors.fromString("{{0,1},{0,0}}");
    Tensor output = ImageRegions.grayscale(image);
    assertEquals(image, output);
  }

  public void testFail() {
    try {
      ImageRegions.loadFromRepository( //
          "/does/not/exist.png", Tensors.vector(10, 10), false);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
