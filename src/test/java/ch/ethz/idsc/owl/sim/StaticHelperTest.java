// code by jph
package ch.ethz.idsc.owl.sim;

import ch.ethz.idsc.tensor.Tensor;
import junit.framework.TestCase;

public class StaticHelperTest extends TestCase {
  public void testSimple() {
    for (int resolution = 2; resolution < 10; ++resolution) {
      Tensor localPoints = StaticHelper.create(resolution);
      assertEquals(localPoints.length(), resolution * resolution);
    }
  }
}
