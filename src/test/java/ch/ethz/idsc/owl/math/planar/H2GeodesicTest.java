package ch.ethz.idsc.owl.math.planar;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class H2GeodesicTest extends TestCase {
  public void testSimple() {
    Tensor split = H2Geodesic.INSTANCE.split(Tensors.vector(1, 1), Tensors.vector(3, 1), RationalScalar.HALF);
    // System.out.println(split);
    assertEquals(split.Get(0), RealScalar.of(2));
  }

  public void testYAxis() {
    Tensor split = H2Geodesic.INSTANCE.split(Tensors.vector(1, 1), Tensors.vector(1, 3), RationalScalar.HALF);
    // System.out.println(split);
    assertEquals(split.Get(0), RealScalar.of(1));
  }
}
