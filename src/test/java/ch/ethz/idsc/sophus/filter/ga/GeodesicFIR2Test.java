// code by ob
package ch.ethz.idsc.sophus.filter.ga;

import java.io.IOException;
import java.util.Arrays;

import ch.ethz.idsc.sophus.filter.CausalFilter;
import ch.ethz.idsc.sophus.lie.se2.Se2Geodesic;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.alg.Last;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class GeodesicFIR2Test extends TestCase {
  public void testTranslation() throws ClassNotFoundException, IOException {
    Tensor p = Tensors.vector(0, 0, 0);
    Tensor q = Tensors.vector(1, 1, 0);
    Tensor r = Tensors.vector(1, 2, 1);
    Scalar alpha = RealScalar.of(0.5);
    Tensor control = Tensors.of(p, q, r);
    TensorUnaryOperator geodesicFIR2 = //
        Serialization.copy(GeodesicFIR2.of(Se2Geodesic.INSTANCE, alpha));
    Tensor refined = Tensor.of(control.stream().map(geodesicFIR2));
    assertEquals(refined.get(1), Tensors.vector(1, 1, 0));
    Chop._12.requireClose(refined.get(2), Tensors.vector(1.5, 2.127670960610518, 0.5));
  }

  public void testRotation() {
    Tensor p = Tensors.vector(0, 0, 0);
    Tensor q = Tensors.vector(0, 0, 2);
    Scalar alpha = RealScalar.of(0.5);
    Tensor control = Tensors.of(p, q);
    TensorUnaryOperator geodesicFIR2 = GeodesicFIR2.of(Se2Geodesic.INSTANCE, alpha);
    Tensor refined = Tensor.of(control.stream().map(geodesicFIR2));
    assertEquals(refined.get(1), Tensors.vector(0, 0, 2));
  }

  public void testCombined() {
    Scalar alpha = RealScalar.of(0.5);
    TensorUnaryOperator causalFilter = //
        CausalFilter.of(() -> GeodesicFIR2.of(Se2Geodesic.INSTANCE, alpha));
    Distribution distribution = NormalDistribution.standard();
    Tensor control = RandomVariate.of(distribution, 100, 3);
    Tensor result = causalFilter.apply(control);
    assertEquals(Dimensions.of(result), Arrays.asList(100, 3));
    control.set(Scalar::zero, 0, Tensor.ALL);
    Tensor passtw = causalFilter.apply(control);
    assertFalse(Chop._11.close(result.get(0), passtw.get(0)));
    assertEquals(result.get(1), passtw.get(1));
    assertFalse(Chop._11.close(result.get(2), passtw.get(2)));
    assertEquals(result.get(3), passtw.get(3));
    assertEquals(result.get(4), passtw.get(4));
    assertEquals(Last.of(result), Last.of(passtw));
  }
}
