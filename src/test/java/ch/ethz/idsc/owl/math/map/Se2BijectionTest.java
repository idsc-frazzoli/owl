// code by jph
package ch.ethz.idsc.owl.math.map;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.mat.Inverse;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class Se2BijectionTest extends TestCase {
  public void testSimple() {
    Bijection bijection = new Se2Bijection(Tensors.vector(2, 3, .3));
    Tensor vector = Tensors.vector(.32, -.98);
    Tensor sameor = bijection.inverse().apply(bijection.forward().apply(vector));
    assertTrue(Chop._14.close(vector, sameor));
  }

  public void testInverse() {
    Se2Bijection se2Bijection = new Se2Bijection(Tensors.vector(2, -3, 1.3));
    Tensor matrix = se2Bijection.forward_se2();
    Tensor se2inv = Inverse.of(matrix);
    Tensor xya = Se2Utils.fromSE2Matrix(se2inv);
    Se2Bijection se2Inverse = new Se2Bijection(xya);
    assertTrue(Chop._14.close(se2Inverse.forward_se2().dot(matrix), IdentityMatrix.of(3)));
    Tensor vector = Tensors.vector(5, 6);
    Tensor imaged = se2Bijection.forward().apply(vector);
    assertTrue(Chop._14.close(se2Inverse.forward().apply(imaged), vector));
  }
}
