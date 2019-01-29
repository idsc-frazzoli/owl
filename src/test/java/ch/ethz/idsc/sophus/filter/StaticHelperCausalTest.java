//code by ob
package ch.ethz.idsc.sophus.filter;

import ch.ethz.idsc.sophus.group.RnGeodesic;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.HilbertMatrix;
import junit.framework.TestCase;

public class StaticHelperCausalTest extends TestCase {
  public void testEquivalence() {
    Tensor linear = Tensors.vector(.2, .3, .5);
    Tensor geodesic = StaticHelperCausal.splits(linear);
    Tensor p = RealScalar.ONE;
    Tensor q = RealScalar.of(4);
    Tensor r = RealScalar.of(2);
    Tensor test = Tensors.of(p, q, r);
    Tensor temp1 = RnGeodesic.INSTANCE.split(p, q, geodesic.Get(0));
    Tensor temp2 = RnGeodesic.INSTANCE.split(temp1, r, geodesic.Get(1));
    assertEquals(test, temp2);
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
