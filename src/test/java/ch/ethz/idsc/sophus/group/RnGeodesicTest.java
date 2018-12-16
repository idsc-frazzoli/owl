// code by jph
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class RnGeodesicTest extends TestCase {
  public void testSimple() {
    Tensor actual = RnGeodesic.INSTANCE.split(Tensors.vector(10, 1), Tensors.vector(11, 0), RealScalar.of(-1));
    assertTrue(ExactScalarQ.all(actual));
    assertEquals(Tensors.vector(9, 2), actual);
  }

  public void testEndPoints() {
    Distribution distribution = NormalDistribution.standard();
    for (int index = 0; index < 10; ++index) {
      Tensor p = RandomVariate.of(distribution, 7);
      Tensor q = RandomVariate.of(distribution, 7);
      assertTrue(Chop._14.close(p, RnGeodesic.INSTANCE.split(p, q, RealScalar.ZERO)));
      assertTrue(Chop._14.close(q, RnGeodesic.INSTANCE.split(p, q, RealScalar.ONE)));
    }
  }
}
