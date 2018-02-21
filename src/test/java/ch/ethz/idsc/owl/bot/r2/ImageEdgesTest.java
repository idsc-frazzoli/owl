// code by jph
package ch.ethz.idsc.owl.bot.r2;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class ImageEdgesTest extends TestCase {
  public void testSimple() {
    Tensor image = ImageEdges.extrusion(Tensors.fromString("{{0,255},{255,0}}"), 1);
    assertEquals(image, Tensors.fromString("{{128, 255}, {255, 128}}"));
  }

  public void testFail() {
    try {
      ImageEdges.extrusion(Tensors.fromString("{{1,255},{255,0}}"), 1);
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
  }
}
