// code by ob
package ch.ethz.idsc.sophus.filter;

import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class GeodesicAverageTest extends TestCase {
  public void testSimple() {
    try {
      GeodesicAverage.of(null, Tensors.vector(1, 2, 3));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
