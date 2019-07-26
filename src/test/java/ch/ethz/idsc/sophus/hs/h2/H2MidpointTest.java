// code by jph
package ch.ethz.idsc.sophus.hs.h2;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class H2MidpointTest extends TestCase {
  public void testSymmetric() {
    Distribution distribution = UniformDistribution.of(-0.8, 0.8);
    for (int count = 0; count < 10; ++count) {
      Tensor a = RandomVariate.of(distribution, 2);
      Tensor midpoint = H2Midpoint.INSTANCE.midpoint(a, a.negate());
      assertTrue(Chop._12.allZero(midpoint));
    }
  }

  public void testLine() {
    Tensor midpoint = H2Midpoint.INSTANCE.midpoint(Tensors.vector(0.5, 0.5), Tensors.vector(0.25, 0.25));
    Chop._12.requireClose(midpoint.Get(0), midpoint.Get(1));
  }
}
