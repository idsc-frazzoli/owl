// code by jph
package ch.ethz.idsc.sophus.hs.spd;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.lie.MatrixExp;
import ch.ethz.idsc.tensor.lie.MatrixLog;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class SpdExponentialTest extends TestCase {
  public void testSimple() {
    for (int n = 1; n < 5; ++n) {
      Tensor x = TestHelper.generateSim(n);
      Tensor g = SpdExponential.INSTANCE.exp(x);
      Tensor r = SpdExponential.INSTANCE.log(g);
      Chop._10.requireClose(x, r);
    }
  }

  public void testMatrixExp() {
    for (int n = 1; n < 5; ++n) {
      Tensor x = TestHelper.generateSim(n);
      Tensor exp1 = SpdExponential.INSTANCE.exp(x);
      Tensor exp2 = MatrixExp.of(x);
      Chop._10.requireClose(exp1, exp2);
    }
  }

  public void testMatrixLog() {
    for (int count = 0; count < 10; ++count) {
      Tensor x = TestHelper.generateSpd(2);
      Tensor exp1 = SpdExponential.INSTANCE.log(x);
      Tensor exp2 = MatrixLog.of(x);
      Chop._08.requireClose(exp1, exp2);
    }
  }
}
