// code by jph
package ch.ethz.idsc.sophus.surf;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Normalize;
import ch.ethz.idsc.tensor.mat.OrthogonalMatrixQ;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Clips;
import junit.framework.TestCase;

public class RotationMatrix3DTest extends TestCase {
  private static final Distribution UNIFORM = UniformDistribution.of(Clips.absoluteOne());
  private static final TensorUnaryOperator NORMALIZE = Normalize.with(Norm._2);

  public void testSimple() {
    Scalar scalar = RealScalar.of(.5);
    Tensor tensor = R3S2Geodesic.INSTANCE.split( //
        Tensors.fromString("{{0,0,0},{1,0,0}}"), //
        Tensors.fromString("{{1,0,0},{0,1,0}}"), scalar);
    Chop._11.requireClose(tensor, //
        Tensors.fromString("{{0.5, -0.20710678118654752, 0.0}, {0.7071067811865476, 0.7071067811865475, 0.0}}"));
  }

  public void testTarget() {
    for (int count = 0; count < 20; ++count) {
      Tensor a = NORMALIZE.apply(RandomVariate.of(UNIFORM, 3));
      Tensor b = NORMALIZE.apply(RandomVariate.of(UNIFORM, 3));
      Tensor rotation = RotationMatrix3D.of(a, b);
      assertTrue(OrthogonalMatrixQ.of(rotation));
      Chop._08.requireClose(rotation.dot(a), b);
    }
  }
}
