// code by jph
package ch.ethz.idsc.sophus.flt.ga;

import ch.ethz.idsc.sophus.lie.rn.RnGeodesic;
import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.img.MeanFilter;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.sca.Unitize;
import junit.framework.TestCase;

public class GeodesicMeanFilterTest extends TestCase {
  public void testSimple() {
    for (int radius = 0; radius < 4; ++radius) {
      TensorUnaryOperator tensorUnaryOperator = GeodesicMeanFilter.of(RnGeodesic.INSTANCE, radius);
      Tensor tensor = Tensors.vector(1, 2, 3, 4, 6, 7);
      Tensor result = tensorUnaryOperator.apply(tensor);
      assertEquals(result.length(), tensor.length());
    }
  }

  public void testRadiusOne() {
    TensorUnaryOperator tensorUnaryOperator = GeodesicMeanFilter.of(RnGeodesic.INSTANCE, 1);
    Tensor tensor = UnitVector.of(10, 5);
    Tensor result = tensorUnaryOperator.apply(tensor);
    assertEquals(Total.of(result), RealScalar.ONE);
    Tensor expect = UnitVector.of(10, 4).add(UnitVector.of(10, 5)).add(UnitVector.of(10, 6));
    assertEquals(Unitize.of(result), expect);
  }

  public void testMultiRadius() {
    for (int radius = 0; radius < 5; ++radius) {
      TensorUnaryOperator tensorUnaryOperator = GeodesicMeanFilter.of(RnGeodesic.INSTANCE, radius);
      Tensor tensor = UnitVector.of(2 * radius + 1, radius);
      Tensor result = tensorUnaryOperator.apply(tensor);
      Tensor expect = MeanFilter.of(tensor, radius);
      assertEquals(result.Get(radius), expect.Get(radius));
      ExactTensorQ.require(result);
    }
  }

  public void testBiUnits() {
    int radius = 2;
    TensorUnaryOperator tensorUnaryOperator = GeodesicMeanFilter.of(RnGeodesic.INSTANCE, radius);
    Tensor tensor = Tensors.vector(0, 0, 0, 0, 1, 0, 4, 0, 0, 0, 0);
    Tensor result = tensorUnaryOperator.apply(tensor);
    Tensor expect = MeanFilter.of(tensor, radius);
    assertEquals(result, expect);
    ExactTensorQ.require(result);
  }
}
