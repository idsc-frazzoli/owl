// code by jph
package ch.ethz.idsc.sophus.lie;

import ch.ethz.idsc.sophus.lie.se2.Se2Group;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class AdjointTest extends TestCase {
  public void testSe2() {
    Se2Group lieGroup = Se2Group.INSTANCE;
    Adjoint adjoint = new Adjoint(lieGroup, IdentityMatrix.of(3));
    Tensor g1 = Tensors.vector(1, 4, -2);
    Tensor m1 = adjoint.matrix(g1);
    Tensor g2 = Tensors.vector(7, -3, 1);
    Tensor m2 = adjoint.matrix(g2);
    Tensor g3 = lieGroup.element(g1).combine(g2);
    Tensor m3 = adjoint.matrix(g3);
    Chop._10.requireClose(m1.dot(m2), m3);
  }
}
