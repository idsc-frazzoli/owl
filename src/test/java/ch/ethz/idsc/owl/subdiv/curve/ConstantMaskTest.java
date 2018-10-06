// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Total;
import junit.framework.TestCase;

public class ConstantMaskTest extends TestCase {
  public void testSimple() {
    for (int width = 0; width < 5; ++width) {
      Tensor tensor = ConstantMask.FUNCTION.apply(width);
      assertEquals(Total.of(tensor), RealScalar.ONE);
    }
  }
}
