// code by jph
package ch.ethz.idsc.sophus.hs.spd;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class SpdExponentialTest extends TestCase {
  public void testSimple() {
    Distribution distribution = UniformDistribution.of(-2, 2);
    Tensor matrix = RandomVariate.of(distribution, 3, 3);
    Tensor x = Transpose.of(matrix).add(matrix);
    Tensor g = SpdExponential.INSTANCE.exp(x);
    Tensor r = SpdExponential.INSTANCE.log(g);
    Chop._10.requireClose(x, r);
  }
}
