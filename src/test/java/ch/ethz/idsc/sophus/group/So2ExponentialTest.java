// code by ob
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.Assert;
import junit.framework.TestCase;

public class So2ExponentialTest extends TestCase {
  public void testSimple() {
    Tensor vector = Tensors.vector(Math.random());
    Tensor m1 = So2Exponential.INSTANCE.exp(vector);
    Tensor m2 = So2Exponential.INSTANCE.exp(vector.negate());
    assertFalse(Chop._12.close(m1, IdentityMatrix.of(2)));
    Chop._12.requireClose(m1.dot(m2), IdentityMatrix.of(2));
  }

  public void testLog() {
    Tensor vector = Tensors.vector(Math.random());
    Tensor matrix = So2Exponential.INSTANCE.exp(vector);
    Tensor result = So2Exponential.INSTANCE.log(matrix);
    Chop._12.requireClose(result, vector);
  }

  public void testTranspose() {
    Tensor vector = Tensors.vector(Math.random());
    Tensor m1 = So2Exponential.INSTANCE.exp(vector);
    Tensor m2 = Transpose.of(So2Exponential.INSTANCE.exp(vector));
    Assert.assertEquals(IdentityMatrix.of(2), m1.dot(m2));
  }
}
