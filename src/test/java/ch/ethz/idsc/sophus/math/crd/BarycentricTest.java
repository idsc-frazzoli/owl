// code by jph
package ch.ethz.idsc.sophus.math.crd;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class BarycentricTest extends TestCase {
  public void testAll() {
    Tensor p = Tensors.vector(2, 3);
    Tensor q = Tensors.vector(6, -3);
    for (Barycentric barycentric : Barycentric.values()) {
      barycentric.distance(p, q);
    }
  }

  public void testWachspress() {
    Scalar distance = Barycentric.WACHSPRESS.distance(Tensors.vector(2, 3), Tensors.vector(6, -3));
    assertEquals(distance, RealScalar.of(50));
  }
}
