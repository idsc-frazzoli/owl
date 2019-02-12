// code by jph
package ch.ethz.idsc.sophus.filter;

import ch.ethz.idsc.sophus.group.RnGeodesic;
import ch.ethz.idsc.sophus.math.SmoothingKernel;
import ch.ethz.idsc.sophus.math.WindowSideSampler;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class GeodesicExtrapolationTest extends TestCase {
  public void testSimple() {
    for (SmoothingKernel smoothingKernel : SmoothingKernel.values()) {
      TensorUnaryOperator tensorUnaryOperator = GeodesicExtrapolation.of(RnGeodesic.INSTANCE, smoothingKernel);
      for (int index = 2; index < 10; ++index) {
        Scalar result = tensorUnaryOperator.apply(Range.of(0, index)).Get();
        Chop._12.requireClose(result, RealScalar.of(index));
      }
    }
  }

  public void testSingle() {
    for (SmoothingKernel smoothingKernel : SmoothingKernel.values()) {
      TensorUnaryOperator tensorUnaryOperator = GeodesicExtrapolation.of(RnGeodesic.INSTANCE, smoothingKernel);
      Scalar result = tensorUnaryOperator.apply(Tensors.vector(10)).Get();
      Chop._12.requireClose(result, RealScalar.of(10));
    }
  }

  public void testSimple2() {
    Tensor mask = Tensors.vector(.5, .5);
    Tensor result = GeodesicExtrapolation.splits(mask);
    assertEquals(Tensors.vector(.5, 3.0), result);
  }

  public void testElaborate() {
    // TODO OB remove
    // try (HtmlUtf8 htmlUtf8 = HtmlUtf8.page(HomeDirectory.Pictures("some.html"))) {
    // htmlUtf8.appendln("<p>adlshfjga sdfklhj</p>");
    // String name = "asdf";
    // htmlUtf8.appendln("<img src='" + name + "' />");
    // }
    WindowSideSampler windowSideSampler = new WindowSideSampler(SmoothingKernel.GAUSSIAN);
    Tensor mask = windowSideSampler.apply(6);
    Tensor result = GeodesicExtrapolation.splits(mask);
    Tensor expect = Tensors.vector( //
        0.6045315182147757, 0.4610592079176246, 0.3765618899029577, 0.3135075836053491, //
        0.2603421497384919, 0.21295939875081005, 1.4534443632073355);
    assertEquals(expect, result);
  }

  public void testNoExtrapolation() {
    Tensor mask = Tensors.vector(1);
    Tensor result = GeodesicExtrapolation.splits(mask);
    assertEquals(Tensors.vector(1), result);
  }

  public void testAffinityFail() {
    Tensor mask = Tensors.vector(.5, .8);
    try {
      GeodesicExtrapolation.splits(mask);
      fail();
    } catch (Exception exception) {
      // ----
    }
  }
}
