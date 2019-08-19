// code by jph
package ch.ethz.idsc.sophus.math;

import ch.ethz.idsc.sophus.hs.r2.Se2ParametricDistance;
import ch.ethz.idsc.sophus.lie.rn.RnMetric;
import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class DistancesTest extends TestCase {
  public void testR2() {
    Tensor tensor = Distances.of(RnMetric.INSTANCE, Tensors.fromString("{{1, 2}, {2, 2}, {2, 4}}"));
    assertEquals(ExactTensorQ.require(tensor), Tensors.vector(1, 2));
  }

  public void testSe2() {
    Tensor tensor = Distances.of(Se2ParametricDistance.INSTANCE, Tensors.fromString("{{1, 2, 3}, {2, 2, 4}, {2, 4, 3}}"));
    Chop._12.requireClose(tensor, Tensors.vector(1.042914821466744, 2.085829642933488));
  }

  public void testR2Single() {
    Tensor tensor = Distances.of(RnMetric.INSTANCE, Tensors.fromString("{{1, 2}}"));
    assertEquals(tensor, Tensors.empty());
  }

  public void testR2SingleFail() {
    try {
      Distances.of(null, Tensors.fromString("{{1, 2}}"));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testScalarFail() {
    try {
      Distances.of(RnMetric.INSTANCE, Pi.HALF);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
