// code by jph
package ch.ethz.idsc.owl.math.group;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class So3ExponentialTest extends TestCase {
  public void testSimple() {
    Tensor vector = Tensors.vector(.2, .3, -.4);
    Tensor m1 = So3Exponential.INSTANCE.exp(vector);
    Tensor m2 = So3Exponential.INSTANCE.exp(vector.negate());
    assertFalse(Chop._12.close(m1, IdentityMatrix.of(3)));
    assertTrue(Chop._12.close(m1.dot(m2), IdentityMatrix.of(3)));
  }

  public void testLog() {
    Tensor vector = Tensors.vector(.2, .3, -.4);
    Tensor matrix = So3Exponential.INSTANCE.exp(vector);
    Tensor result = So3Exponential.INSTANCE.log(matrix);
    assertEquals(result, vector);
  }
}
