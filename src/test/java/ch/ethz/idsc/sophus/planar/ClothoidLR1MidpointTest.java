// code by jph
package ch.ethz.idsc.sophus.planar;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class ClothoidLR1MidpointTest extends TestCase {
  public void testSimple() {
    Tensor midpoint = ClothoidLR1Midpoint.INSTANCE.midpoint(Tensors.vector(1, 2, 0), Tensors.vector(9, 2, 0));
    assertEquals(midpoint, Tensors.vector(5, 2, 0));
  }
}
