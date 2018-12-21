// code by jph
package ch.ethz.idsc.owl.math;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class SimpleTensorMetricTest extends TestCase {
  public void testSimple() {
    SimpleTensorMetric simpleTensorMetric = new SimpleTensorMetric((a, b) -> a.subtract(b));
    Scalar scalar = simpleTensorMetric.distance(Tensors.vector(1, 2, 3), Tensors.vector(1 + 3, 2 + 4, 3));
    assertEquals(scalar, RealScalar.of(5));
  }
}
