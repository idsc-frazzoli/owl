// code by jph
package ch.ethz.idsc.sophus.lie.sc;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class ScExponentialTest extends TestCase {
  public void testSimple() {
    Distribution distribution = UniformDistribution.of(-500, 500);
    for (int count = 0; count < 100; ++count) {
      Scalar x = RandomVariate.of(distribution);
      Scalar exp = ScExponential.INSTANCE.exp(x);
      Scalar log = ScExponential.INSTANCE.log(exp);
      Chop._10.requireClose(x, log);
    }
  }
}
