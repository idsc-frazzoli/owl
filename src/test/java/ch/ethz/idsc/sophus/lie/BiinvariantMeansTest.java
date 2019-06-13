// code by jph
package ch.ethz.idsc.sophus.lie;

import ch.ethz.idsc.sophus.lie.so2c.So2CoveringBiinvariantMean;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class BiinvariantMeansTest extends TestCase {
  public void testNullFail() {
    try {
      BiinvariantMeans.of(null, Tensors.vector(1));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testNonAffineFail() {
    try {
      BiinvariantMeans.of(So2CoveringBiinvariantMean.INSTANCE, Tensors.vector(1, 1, 1));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
