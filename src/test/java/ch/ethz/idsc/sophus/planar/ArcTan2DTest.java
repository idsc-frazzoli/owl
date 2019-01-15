// code by jph
package ch.ethz.idsc.sophus.planar;

import java.util.Optional;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.lie.LieAlgebras;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.red.VectorAngle;
import ch.ethz.idsc.tensor.sca.ArcTan;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class ArcTan2DTest extends TestCase {
  public void testVectorXY() {
    assertEquals(ArcTan2D.of(Tensors.vector(-1, -2)), ArcTan.of(-1, -2));
    assertEquals(ArcTan2D.of(Tensors.vector(-1, -2, 3)), ArcTan.of(-1, -2));
  }

  public void testVectorAngle() {
    Distribution distribution = UniformDistribution.of(-1, 1);
    Tensor v = UnitVector.of(2, 0);
    for (int count = 0; count < 10; ++count) {
      Tensor u = RandomVariate.of(distribution, 2);
      Optional<Scalar> optional = VectorAngle.of(u, v);
      Scalar scalar = ArcTan2D.of(u);
      Chop._10.requireClose(scalar.abs(), optional.get());
    }
  }

  public void testVectorXYFail() {
    try {
      ArcTan2D.of(Tensors.vector(1));
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      ArcTan2D.of(LieAlgebras.se2());
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
