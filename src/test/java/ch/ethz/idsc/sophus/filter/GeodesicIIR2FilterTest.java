// code by ob and jph
package ch.ethz.idsc.sophus.filter;

import ch.ethz.idsc.sophus.group.LieGroupGeodesic;
import ch.ethz.idsc.sophus.group.RnExponential;
import ch.ethz.idsc.sophus.group.RnGroup;
import ch.ethz.idsc.sophus.group.Se2CoveringExponential;
import ch.ethz.idsc.sophus.group.Se2Group;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class GeodesicIIR2FilterTest extends TestCase {
  public void testSimple() {
    GeodesicInterface geodesicInterface = //
        new LieGroupGeodesic(Se2Group.INSTANCE::element, Se2CoveringExponential.INSTANCE);
    Scalar alpha = RationalScalar.HALF;
    GeodesicIIR2Filter geodesicCausal1Filter = new GeodesicIIR2Filter(geodesicInterface, alpha);
    Tensor vector0 = Tensors.vector(1, 2, 0.25);
    Tensor res0 = geodesicCausal1Filter.apply(vector0);
    assertEquals(res0, vector0);
    Tensor vector1 = Tensors.vector(4, 5, 0.5);
    Tensor res1 = geodesicCausal1Filter.apply(vector1);
    // System.out.println(res1);
    assertTrue(Chop._10.close(res1, Tensors.vector(2.593872261349412, 3.406127738650588, 0.375)));
    // Tensor extrapolate = geodesicCausal1Filter.extrapolate();
    // Tensor expected = Tensors.vector(6.164525387368366, 8.648949142895502, 0.75);
    // assertTrue(Chop._10.close(extrapolate, expected));
    // Tensor filtered = geodesicCausal1Filter.apply(expected);
    // assertTrue(Chop._10.close(filtered, expected));
  }

  public void testLinear() {
    GeodesicInterface geodesicInterface = //
        new LieGroupGeodesic(RnGroup.INSTANCE::element, RnExponential.INSTANCE);
    Scalar alpha = RationalScalar.HALF;
    TensorUnaryOperator tensorUnaryOperator = new GeodesicIIR2Filter(geodesicInterface, alpha);
    assertEquals(tensorUnaryOperator.apply(RealScalar.of(10)), RealScalar.of(10));
    assertEquals(tensorUnaryOperator.apply(RealScalar.of(10)), RealScalar.of(10));
    assertEquals(tensorUnaryOperator.apply(RealScalar.of(20)), RealScalar.of(15));
    {
      Tensor tensor = tensorUnaryOperator.apply(RealScalar.of(20));
      ExactScalarQ.require(tensor.Get());
      assertEquals(tensor, RealScalar.of(20));
    }
    assertEquals(tensorUnaryOperator.apply(RealScalar.of(20.)), RealScalar.of(22.5));
    assertEquals(tensorUnaryOperator.apply(RealScalar.of(20.)), RealScalar.of(22.5));
    assertEquals(tensorUnaryOperator.apply(RealScalar.of(20.)), RealScalar.of(21.25));
    assertEquals(tensorUnaryOperator.apply(RealScalar.of(20.)), RealScalar.of(20));
    assertEquals(tensorUnaryOperator.apply(RealScalar.of(20.)), RealScalar.of(19.375));
  }
}
