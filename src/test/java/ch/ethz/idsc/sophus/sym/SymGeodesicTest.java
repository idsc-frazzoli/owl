// code by jph
package ch.ethz.idsc.sophus.sym;

import java.util.stream.IntStream;

import ch.ethz.idsc.sophus.filter.GeodesicCenter;
import ch.ethz.idsc.sophus.math.SmoothingKernel;
import ch.ethz.idsc.sophus.math.WindowCenterSampler;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import junit.framework.TestCase;

public class SymGeodesicTest extends TestCase {
  public void testSimple() {
    Scalar s1 = SymScalar.leaf(1);
    Scalar s2 = SymScalar.leaf(2);
    SymScalar s3 = (SymScalar) SymScalar.of(s1, s2, RationalScalar.HALF);
    Scalar scalar = SymScalar.of(s1, s2, RationalScalar.of(1, 2));
    assertEquals(s3, scalar);
    Scalar evaluate = s3.evaluate();
    assertEquals(evaluate, RationalScalar.of(3, 2));
    WindowCenterSampler centerWindowSampler = new WindowCenterSampler(SmoothingKernel.DIRICHLET);
    TensorUnaryOperator tensorUnaryOperator = //
        GeodesicCenter.of(SymGeodesic.INSTANCE, centerWindowSampler);
    Tensor vector = Tensor.of(IntStream.range(0, 5).mapToObj(SymScalar::leaf));
    Tensor tensor = tensorUnaryOperator.apply(vector);
    assertEquals(tensor.toString(), "{{{0, 1, 1/2}, 2, 1/5}, {{4, 3, 1/2}, 2, 1/5}, 1/2}");
    SymLink root = SymLink.build((SymScalar) tensor);
    Tensor pose = root.getPosition();
    assertEquals(pose, Tensors.vector(2, -1.5));
    SymScalar res = (SymScalar) tensor;
    assertEquals(res.evaluate(), RealScalar.of(2));
  }
}
