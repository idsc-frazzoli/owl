// code by jph
package ch.ethz.idsc.sophus.flt.ts;

import java.io.IOException;

import ch.ethz.idsc.sophus.crv.spline.MonomialExtrapolationMask;
import ch.ethz.idsc.sophus.flt.WindowSideExtrapolation;
import ch.ethz.idsc.sophus.lie.rn.RnExponential;
import ch.ethz.idsc.sophus.lie.rn.RnGeodesic;
import ch.ethz.idsc.sophus.lie.rn.RnGroup;
import ch.ethz.idsc.sophus.math.win.SmoothingKernel;
import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class TangentSpaceIIRnFilterTest extends TestCase {
  public void testMonomial() throws ClassNotFoundException, IOException {
    for (int radius = 0; radius < 6; ++radius) {
      TensorUnaryOperator tensorUnaryOperator = Serialization.copy(TangentSpaceIIRnFilter.of( //
          RnGroup.INSTANCE, RnExponential.INSTANCE, MonomialExtrapolationMask.INSTANCE, RnGeodesic.INSTANCE, radius, RationalScalar.HALF));
      Tensor signal = Range.of(0, 10);
      Tensor tensor = tensorUnaryOperator.apply(signal);
      assertEquals(signal, tensor);
      ExactTensorQ.require(tensor);
    }
  }

  public void testKernel() throws ClassNotFoundException, IOException {
    for (SmoothingKernel smoothingKernel : SmoothingKernel.values())
      for (int radius = 0; radius < 6; ++radius) {
        TensorUnaryOperator tensorUnaryOperator = Serialization.copy(TangentSpaceIIRnFilter.of( //
            RnGroup.INSTANCE, RnExponential.INSTANCE, WindowSideExtrapolation.of(smoothingKernel), RnGeodesic.INSTANCE, radius, RationalScalar.HALF));
        Tensor signal = Range.of(0, 10);
        Tensor tensor = tensorUnaryOperator.apply(signal);
        Chop._10.requireClose(tensor, signal);
      }
  }
}
