// code by astoll
package ch.ethz.idsc.owl.math.order;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class TotalOrderMinMaxTest extends TestCase {
  private static final Tensor test = Tensors.vector(1, 2, 3, 4, 0.2).unmodifiable();
  private static final Scalar maxExpected = RealScalar.of(4);
  private static final Scalar minExpected = RealScalar.of(0.2);

  public void testMin() {
    assertEquals(minExpected, TotalOrderMinMax.min(test));
  }

  public void testMax() {
    assertEquals(maxExpected, TotalOrderMinMax.max(test));
  }
}
