// code by jph
package ch.ethz.idsc.owl.gui.ren;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.mat.HilbertMatrix;
import junit.framework.TestCase;

public class GridRenderTest extends TestCase {
  public void testFailMatrix() {
    try {
      new GridRender(HilbertMatrix.of(3), HilbertMatrix.of(4));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testFailScalar() {
    try {
      new GridRender(RealScalar.ONE, RealScalar.ZERO);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testFailColorNull() {
    try {
      new GridRender(Subdivide.of(1, 2, 3), Subdivide.of(1, 2, 3), null);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
