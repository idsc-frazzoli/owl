// code by jph
package ch.ethz.idsc.sophus.math.crd;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class BarycentricTest extends TestCase {
  public void testWachspress() {
    Scalar distance = Barycentric.WACHSPRESS.distance(Tensors.vector(2, 3), Tensors.vector(6, -3));
    assertEquals(distance, RealScalar.of(50));
  }
}
