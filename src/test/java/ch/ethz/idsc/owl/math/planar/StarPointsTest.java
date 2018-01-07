// code by jph
package ch.ethz.idsc.owl.math.planar;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import junit.framework.TestCase;

public class StarPointsTest extends TestCase {
  public void testSimple() {
    Tensor polygon = StarPoints.of(4, RealScalar.ONE, RealScalar.of(.3));
    assertEquals(polygon.length(), 16);
  }
}
