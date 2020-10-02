// code by jph
package ch.ethz.idsc.owl.gui.ren;

import ch.ethz.idsc.owl.math.AssertFail;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.mat.HilbertMatrix;
import junit.framework.TestCase;

public class GridRenderTest extends TestCase {
  public void testFailMatrix() {
    AssertFail.of(() -> new GridRender(HilbertMatrix.of(3), HilbertMatrix.of(4)));
  }

  public void testFailScalar() {
    AssertFail.of(() -> new GridRender(RealScalar.ONE, RealScalar.ZERO));
  }

  public void testFailColorNull() {
    AssertFail.of(() -> new GridRender(Subdivide.of(1, 2, 3), Subdivide.of(1, 2, 3), null));
  }
}
