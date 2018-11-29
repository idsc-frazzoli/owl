// code by jph
package ch.ethz.idsc.owl.math.planar;

import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.lie.LieAlgebras;
import ch.ethz.idsc.tensor.sca.ArcTan;
import junit.framework.TestCase;

public class ArcTan2DTest extends TestCase {
  public void testVectorXY() {
    assertEquals(ArcTan2D.of(Tensors.vector(-1, -2)), ArcTan.of(-1, -2));
    assertEquals(ArcTan2D.of(Tensors.vector(-1, -2, 3)), ArcTan.of(-1, -2));
  }

  public void testVectorXYFail() {
    try {
      ArcTan2D.of(Tensors.vector(1));
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      ArcTan2D.of(LieAlgebras.se2());
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
