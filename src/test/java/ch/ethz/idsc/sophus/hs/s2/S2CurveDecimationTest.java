// code by jph
package ch.ethz.idsc.sophus.hs.s2;

import java.io.IOException;

import ch.ethz.idsc.sophus.crv.CurveDecimation;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Normalize;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Norm;
import junit.framework.TestCase;

public class S2CurveDecimationTest extends TestCase {
  private static final TensorUnaryOperator NORMALIZE = Normalize.with(Norm._2);

  public void testGeodesic() {
    Tensor p0 = NORMALIZE.apply(Tensors.vector(1, 0, 0));
    Tensor p1 = NORMALIZE.apply(Tensors.vector(0.5, 0.5, 0));
    Tensor p2 = NORMALIZE.apply(Tensors.vector(0, 1, 0));
    Tensor tensor = Tensors.of(p0, p1, p2);
    CurveDecimation curveDecimation = S2CurveDecimation.of(RealScalar.of(0.1));
    Tensor result = curveDecimation.apply(tensor);
    assertEquals(result, Tensors.of(UnitVector.of(3, 0), UnitVector.of(3, 1)));
  }

  public void testTriangle() throws ClassNotFoundException, IOException {
    Tensor p0 = NORMALIZE.apply(Tensors.vector(1, 0, 0));
    Tensor p1 = NORMALIZE.apply(Tensors.vector(0.5, 0.5, 0.8));
    Tensor p2 = NORMALIZE.apply(Tensors.vector(0, 1, 0));
    Tensor tensor = Tensors.of(p0, p1, p2);
    CurveDecimation curveDecimation = Serialization.copy(S2CurveDecimation.of(RealScalar.of(0.1)));
    Tensor result = curveDecimation.apply(tensor);
    assertEquals(result, tensor);
  }
}
