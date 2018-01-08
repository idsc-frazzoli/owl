// code by jph
package ch.ethz.idsc.owl.math.map;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import junit.framework.TestCase;

public class IdentityFamilyTest extends TestCase {
  public void testForwardSe2() {
    assertEquals(IdentityFamily.INSTANCE.forward_se2(RealScalar.of(-312.32)), IdentityMatrix.of(3));
  }

  public void testForward() {
    Tensor vector = Tensors.vector(2, 3, 4, 9, 10);
    assertEquals(IdentityFamily.INSTANCE.forward(RealScalar.of(-312.32)).apply(vector), vector);
  }

  public void testInverse() {
    Tensor vector = Tensors.vector(2, 3, 4, 9, 10);
    assertEquals(IdentityFamily.INSTANCE.inverse(RealScalar.of(-312.32)).apply(vector), vector);
  }
}
