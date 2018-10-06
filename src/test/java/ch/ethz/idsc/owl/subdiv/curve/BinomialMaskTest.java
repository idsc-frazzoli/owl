// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Total;
import junit.framework.TestCase;

public class BinomialMaskTest extends TestCase {
  public void testSimple() {
    for (int size = 0; size < 5; ++size) {
      Tensor mask = BinomialMask.FUNCTION.apply(size);
      assertEquals(Total.of(mask), RealScalar.ONE);
      assertTrue(ExactScalarQ.all(mask));
    }
  }
}
