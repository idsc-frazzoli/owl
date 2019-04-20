// code by jph
package ch.ethz.idsc.sophus.group;

import java.util.Arrays;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.lie.Rodrigues;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class Se3GroupElementTest extends TestCase {
  public void testSimple() {
    Tensor R = Rodrigues.exp(Tensors.vector(-1, -.2, .3));
    Tensor t = Tensors.vector(4, 5, 6);
    Se3GroupElement g = new Se3GroupElement(R, t);
    Tensor adjoint = g.inverse().adjoint(Tensors.fromString("{{1,2,3},{4,5,6}}"));
    assertEquals(Dimensions.of(adjoint), Arrays.asList(2, 3));
    Tensor ge = g.combine(IdentityMatrix.of(4));
    Chop._10.requireClose(Se3Utils.rotation(ge), R);
    Chop._10.requireClose(Se3Utils.translation(ge), t);
    Se3GroupElement e = new Se3GroupElement(IdentityMatrix.of(4));
    Tensor eg = e.combine(Se3Utils.toMatrix4x4(R, t));
    Chop._10.requireClose(Se3Utils.rotation(eg), R);
    Chop._10.requireClose(Se3Utils.translation(eg), t);
  }
}
