// code by jph
package ch.ethz.idsc.sophus.app.api;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.VectorQ;
import junit.framework.TestCase;

public class GeodesicDisplaysTest extends TestCase {
  public void testSimple() {
    assertTrue(4 <= GeodesicDisplays.ALL.size());
  }

  public void testToPoint() {
    for (GeodesicDisplay geodesicDisplay : GeodesicDisplays.ALL) {
      Tensor xya = Tensors.vector(1, 2, 3);
      Tensor p = geodesicDisplay.project(xya);
      VectorQ.requireLength(geodesicDisplay.toPoint(p), 2);
    }
  }
}
