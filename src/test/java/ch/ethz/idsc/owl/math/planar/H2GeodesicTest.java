// code by ob
package ch.ethz.idsc.owl.math.planar;

import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class H2GeodesicTest extends TestCase {
  public void testSimple() {
    Tensor split = H2Geodesic.INSTANCE.split(Tensors.vector(1, 1), Tensors.vector(3, 1), RationalScalar.HALF);
    assertTrue(Chop._12.close(split, Tensors.vector(2, Math.sqrt(2))));
  }

  public void testYAxis() {
    Tensor split = H2Geodesic.INSTANCE.split(Tensors.vector(1, 1), Tensors.vector(1, 3), RationalScalar.HALF);
    assertTrue(ExactScalarQ.of(split.Get(0)));
    assertTrue(Chop._12.close(split, Tensors.vector(1, 1.7320508075688772)));
  }

  public void testSingularityExact() {
    try {
      H2Geodesic.INSTANCE.split(Tensors.vector(1, 0), Tensors.vector(1, 3), RationalScalar.HALF);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testSingularityNumeric() {
    try {
      H2Geodesic.INSTANCE.split(Tensors.vector(1, 0.0), Tensors.vector(1, 3), RationalScalar.HALF);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
