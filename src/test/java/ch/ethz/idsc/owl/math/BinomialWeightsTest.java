// code by jph
package ch.ethz.idsc.owl.math;

import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Total;
import junit.framework.TestCase;

public class BinomialWeightsTest extends TestCase {
  public void testBinomial() {
    for (int size = 0; size < 5; ++size) {
      Tensor mask = BinomialWeights.INSTANCE.apply(size);
      assertEquals(Total.of(mask), RealScalar.ONE);
      assertTrue(ExactScalarQ.all(mask));
    }
  }

  public void testSpecific() {
    Tensor result = BinomialWeights.INSTANCE.apply(2);
    Tensor expect = Tensors.fromString("{1/16, 1/4, 3/8, 1/4, 1/16}");
    assertEquals(result, expect);
  }
}
