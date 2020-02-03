// code by jph
package ch.ethz.idsc.owl.bot.kl;

import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class MirrorYStateTimeRasterTest extends TestCase {
  public void testSimple() {
    MirrorYStateTimeRaster mirrorYStateTimeRaster = new MirrorYStateTimeRaster(4);
    assertEquals(mirrorYStateTimeRaster.mirrorStone(Tensors.vector(0, 9, 0)), Tensors.vector(0, 9, 2));
    assertEquals(mirrorYStateTimeRaster.mirrorStone(Tensors.vector(1, 0, 0)), Tensors.vector(1, 0, 3));
    assertEquals(mirrorYStateTimeRaster.mirrorStone(Tensors.vector(2, 0, 0)), Tensors.vector(2, 0, 2));
    assertEquals(mirrorYStateTimeRaster.mirrorStone(Tensors.vector(3, 0, 0)), Tensors.vector(3, 0, 3));
    assertEquals(mirrorYStateTimeRaster.mirrorStone(Tensors.vector(0, 0, 1)), Tensors.vector(0, 0, 1));
    assertEquals(mirrorYStateTimeRaster.mirrorStone(Tensors.vector(1, 0, 1)), Tensors.vector(1, 0, 2));
    assertEquals(mirrorYStateTimeRaster.mirrorStone(Tensors.vector(2, 0, 1)), Tensors.vector(2, 0, 1));
    assertEquals(mirrorYStateTimeRaster.mirrorStone(Tensors.vector(3, 0, 1)), Tensors.vector(3, 0, 2));
  }
}
