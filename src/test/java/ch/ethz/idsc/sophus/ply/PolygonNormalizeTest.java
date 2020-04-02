// code by jph
package ch.ethz.idsc.sophus.ply;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class PolygonNormalizeTest extends TestCase {
  public void testSimple() {
    Tensor polygon = Tensors.fromString("{{0, 0}, {2, 0}, {2, 2}, {0, 2}}");
    Tensor tensor = PolygonNormalize.of(polygon, RealScalar.ONE);
    assertEquals(tensor.toString(), "{{-1/2, -1/2}, {1/2, -1/2}, {1/2, 1/2}, {-1/2, 1/2}}");
  }
}
