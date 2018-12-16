// code by jph
package ch.ethz.idsc.sophus.curve;

import java.util.Arrays;

import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import junit.framework.TestCase;

public class BSplineLimitMatrixTest extends TestCase {
  public void testLinear() {
    for (int n = 1; n < 6; ++n) {
      Tensor tensor = BSplineLimitMatrix.of(1, n);
      assertEquals(tensor, IdentityMatrix.of(n));
      assertTrue(ExactScalarQ.all(tensor));
    }
  }

  public void testQuadratic() {
    for (int n = 1; n <= 6; ++n) {
      Tensor vector = Array.of(l -> RealScalar.ONE, n);
      Tensor tensor = BSplineLimitMatrix.of(2, n);
      assertEquals(Dimensions.of(tensor), Arrays.asList(n, n));
      assertEquals(tensor.dot(vector), vector);
      assertTrue(ExactScalarQ.all(tensor));
    }
  }

  public void testCubic() {
    for (int n = 1; n <= 6; ++n) {
      Tensor vector = Array.of(l -> RealScalar.ONE, n);
      Tensor tensor = BSplineLimitMatrix.of(3, n);
      assertEquals(Dimensions.of(tensor), Arrays.asList(n, n));
      assertEquals(tensor.dot(vector), vector);
      assertTrue(ExactScalarQ.all(tensor));
    }
  }
}
