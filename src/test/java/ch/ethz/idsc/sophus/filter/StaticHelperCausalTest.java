//code by ob
package ch.ethz.idsc.sophus.filter;

import ch.ethz.idsc.sophus.group.RnGeodesic;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class StaticHelperCausalTest extends TestCase {
  public void testEquivalence() {
    Tensor linear = Tensors.vector(.2, .3, .5);
    Tensor geodesic = StaticHelperCausal.splits(linear);
    Tensor p = RealScalar.ONE;
    Tensor q = RealScalar.of(4);
    Tensor r = RealScalar.of(2);
    Tensor geodesicavg = RnGeodesic.INSTANCE.split(RnGeodesic.INSTANCE.split(p, q, geodesic.Get(0)), r, geodesic.Get(1));
    Tensor dot = Tensors.of(p, q, r).dot(linear);
    assertEquals(geodesicavg, dot);
  }

  public void testScalarFail() {
    try {
      StaticHelperCausal.splits(RealScalar.ONE);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  // public void testMatrixFail() {
  // try {
  // StaticHelperCausal.splits(HilbertMatrix.of(2, 3));
  // fail();
  // } catch (Exception exception) {
  // // ---
  // }
  // }
  public void testEmptyFail() {
    try {
      StaticHelperCausal.splits(Tensors.empty());
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
