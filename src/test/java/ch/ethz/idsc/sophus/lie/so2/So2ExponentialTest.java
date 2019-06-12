// code by ob
package ch.ethz.idsc.sophus.lie.so2;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import junit.framework.TestCase;

public class So2ExponentialTest extends TestCase {
  public void testSimple() {
    Distribution distribution = UniformDistribution.unit();
    Scalar alpha = RandomVariate.of(distribution);
    assertEquals(alpha, So2Exponential.INSTANCE.log(So2Exponential.INSTANCE.exp(alpha)));
  }
}
