// code by astoll
package ch.ethz.idsc.owl.math.order;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class TotalOrderMinMaxTest extends TestCase {
  public void testMin() {
    Tensor test = Tensors.vector(1, 2, 3, 4, 0.2).unmodifiable();
    assertEquals(RealScalar.of(0.2), TotalOrderMinMax.min(test));
    assertEquals(RealScalar.of(4), TotalOrderMinMax.max(test));
  }
}
