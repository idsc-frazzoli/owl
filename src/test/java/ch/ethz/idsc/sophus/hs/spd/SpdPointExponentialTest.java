// code by jph
package ch.ethz.idsc.sophus.hs.spd;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class SpdPointExponentialTest extends TestCase {
  public void testSimple() {
    for (int n = 1; n < 5; ++n) {
      Tensor p = TestHelper.generateSpd(n);
      Tensor q = TestHelper.generateSpd(n);
      Tensor w = SpdPointExponential.log(p, q);
      Tensor exp = SpdPointExponential.exp(p, w);
      Chop._08.requireClose(q, exp);
    }
  }

  public void testMidpoint() {
    for (int n = 1; n < 5; ++n) {
      Tensor p = TestHelper.generateSpd(n);
      Tensor q = TestHelper.generateSpd(n);
      Tensor pqw = SpdPointExponential.log(p, q);
      Tensor qpw = SpdPointExponential.log(q, p);
      Tensor ph = SpdPointExponential.exp(p, pqw.multiply(RationalScalar.HALF));
      Tensor qh = SpdPointExponential.exp(q, qpw.multiply(RationalScalar.HALF));
      Chop._08.requireClose(ph, qh);
    }
  }
}
