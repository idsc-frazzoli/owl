// code by jph
package ch.ethz.idsc.sophus.srf.subdiv;

import ch.ethz.idsc.sophus.math.AffineQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class DooSabinWeightsTest extends TestCase {
  public void testSimple() {
    for (int n = 3; n <= 12; ++n) {
      Tensor w = DooSabinWeights.instance().apply(n);
      AffineQ.require(w);
      Chop._12.requireClose(w, DooSabinWeights.numeric(n));
    }
  }
}
