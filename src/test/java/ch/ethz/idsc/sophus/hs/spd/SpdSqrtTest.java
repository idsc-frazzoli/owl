// code by jph
package ch.ethz.idsc.sophus.hs.spd;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class SpdSqrtTest extends TestCase {
  public void testSimple() {
    for (int n = 1; n < 6; ++n) {
      Tensor g = TestHelper.generateSpd(n);
      Tensor sqrt = SpdSqrt.of(g);
      Chop._10.requireClose(sqrt.dot(sqrt), g);
    }
  }
}
