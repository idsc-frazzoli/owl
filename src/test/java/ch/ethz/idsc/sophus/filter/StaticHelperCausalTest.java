//code by ob
package ch.ethz.idsc.sophus.filter;

import java.util.function.Function;

import ch.ethz.idsc.sophus.group.RnGeodesic;
import ch.ethz.idsc.sophus.math.SmoothingKernel;
import ch.ethz.idsc.sophus.math.WindowSideSampler;
import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.HilbertMatrix;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class StaticHelperCausalTest extends TestCase {
  public void testEquivalence() {
    // Tensor linear = Tensors.vector(.2, .3, .5);
    Tensor linear = Tensors.vector(Math.random(), Math.random(), Math.random());
    linear = linear.divide(linear.Get(0).add(linear.Get(1).add(linear.Get(2))));
    Tensor geodesic = StaticHelperCausal.splits(linear);
    Tensor p = RealScalar.of(Math.random());
    Tensor q = RealScalar.of(4 * Math.random());
    Tensor r = RealScalar.of(-2 * Math.random());
    Tensor geodesicavg = RnGeodesic.INSTANCE.split(RnGeodesic.INSTANCE.split(p, q, geodesic.Get(0)), r, geodesic.Get(1));
    Tensor dot = Tensors.of(p, q, r).dot(linear);
    Chop._12.requireClose(geodesicavg, dot);
  }

  public void testExact() {
    Function<Integer, Tensor> windowSideSampler = WindowSideSampler.of(SmoothingKernel.HANN);
    Tensor mask = windowSideSampler.apply(2);
    Tensor splits = StaticHelperCausal.splits(mask);
    ExactTensorQ.require(splits);
  }

  public void testNonAffineFail() {
    try {
      StaticHelperCausal.splits(Tensors.vector(.4, .5, .9));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testScalarFail() {
    try {
      StaticHelperCausal.splits(RealScalar.ONE);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testMatrixFail() {
    try {
      StaticHelperCausal.splits(HilbertMatrix.of(2, 3));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testEmptyFail() {
    try {
      StaticHelperCausal.splits(Tensors.empty());
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
