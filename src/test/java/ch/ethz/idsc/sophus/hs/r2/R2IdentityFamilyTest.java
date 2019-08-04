// code by jph
package ch.ethz.idsc.sophus.hs.r2;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import junit.framework.TestCase;

public class R2IdentityFamilyTest extends TestCase {
  public void testForwardSe2() {
    assertEquals(R2IdentityFamily.INSTANCE.forward_se2(RealScalar.of(-312.32)), IdentityMatrix.of(3));
  }

  public void testForward() {
    Tensor vector = Tensors.vector(2, 3, 4, 9, 10);
    assertEquals(R2IdentityFamily.INSTANCE.forward(RealScalar.of(-312.32)).apply(vector), vector);
  }

  public void testInverse() {
    Tensor vector = Tensors.vector(2, 3, 4, 9, 10);
    assertEquals(R2IdentityFamily.INSTANCE.inverse(RealScalar.of(-312.32)).apply(vector), vector);
  }
}
