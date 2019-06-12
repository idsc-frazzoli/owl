// code by jph
package ch.ethz.idsc.sophus.lie.so2c;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class So2CoveringSkewTest extends TestCase {
  public void testZero() {
    Tensor tensor = So2CoveringSkew.of(RealScalar.ZERO);
    assertEquals(tensor, IdentityMatrix.of(2));
  }

  public void testFunctionM() {
    for (Tensor _angle : Subdivide.of(-4, 4, 20)) {
      Scalar angle = _angle.Get();
      Tensor m1 = So2CoveringSkew.of(angle);
      Tensor m2 = So2CoveringSkew.of(angle.negate());
      Chop._10.requireClose(m1, Transpose.of(m2));
    }
  }

  public void testSingularity() {
    Tensor tensor = So2CoveringSkew.of(Pi.VALUE);
    Tensor matrix = Tensors.matrix(new Scalar[][] { { RealScalar.ZERO, Pi.HALF }, { Pi.HALF.negate(), RealScalar.ZERO } });
    Chop._14.requireClose(tensor, matrix);
  }
}
