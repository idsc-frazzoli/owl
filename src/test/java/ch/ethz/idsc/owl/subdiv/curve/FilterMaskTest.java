// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Total;
import junit.framework.TestCase;

public class FilterMaskTest extends TestCase {
  public void testConstant() {
    for (int width = 0; width < 5; ++width) {
      Tensor tensor = FilterMask.CONSTANT.apply(width);
      assertEquals(Total.of(tensor), RealScalar.ONE);
    }
  }

  public void testBinomial() {
    for (int size = 0; size < 5; ++size) {
      Tensor mask = FilterMask.BINOMIAL.apply(size);
      assertEquals(Total.of(mask), RealScalar.ONE);
      assertTrue(ExactScalarQ.all(mask));
    }
  }

  public void testSpecific() {
    Tensor result = FilterMask.BINOMIAL.apply(2);
    Tensor expect = Tensors.fromString("{1/16, 1/4, 3/8, 1/4, 1/16}");
    assertEquals(result, expect);
  }

  public void testHamming() {
    for (int size = 0; size < 5; ++size) {
      Tensor result = FilterMask.HAMMING.apply(size);
      assertEquals(result.length(), 2 * size + 1);
    }
  }
}
