// code by jph
package ch.ethz.idsc.sophus.filter.ts;

import java.io.IOException;

import ch.ethz.idsc.sophus.crv.spline.MonomialExtrapolationMask;
import ch.ethz.idsc.sophus.filter.WindowSydeExtrapolation;
import ch.ethz.idsc.sophus.lie.rn.RnExponential;
import ch.ethz.idsc.sophus.lie.rn.RnGeodesic;
import ch.ethz.idsc.sophus.lie.rn.RnGroup;
import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.win.GaussianWindow;
import junit.framework.TestCase;

public class TangentSpaceFIRnFilterTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    for (int radius = 0; radius < 6; ++radius) {
      TensorUnaryOperator tensorUnaryOperator = Serialization.copy(TangentSpaceFIRnFilter.of( //
          RnGroup.INSTANCE, RnExponential.INSTANCE, MonomialExtrapolationMask.INSTANCE, RnGeodesic.INSTANCE, radius, RationalScalar.HALF));
      Tensor signal = Range.of(0, 10);
      Tensor tensor = tensorUnaryOperator.apply(signal);
      assertEquals(signal, tensor);
      ExactTensorQ.require(tensor);
    }
  }

  public void testKernel() throws ClassNotFoundException, IOException {
    for (int radius = 0; radius < 6; ++radius) {
      TensorUnaryOperator tensorUnaryOperator = Serialization.copy(TangentSpaceFIRnFilter.of( //
          RnGroup.INSTANCE, RnExponential.INSTANCE, WindowSydeExtrapolation.of(GaussianWindow.FUNCTION), RnGeodesic.INSTANCE, radius, RationalScalar.HALF));
      Tensor signal = Range.of(0, 10);
      Tensor tensor = tensorUnaryOperator.apply(signal);
      Chop._10.requireClose(tensor, signal);
    }
  }
}
