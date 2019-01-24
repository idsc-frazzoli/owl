//code by ob
package ch.ethz.idsc.sophus.filter;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.HilbertMatrix;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class StaticHelperCausalTest extends TestCase {
  public void testSimple() {
    Tensor mask = Tensors.vector(.2, .4, .9, .5, .5);
    Tensor correct = Tensors.vector(0.012, 0.003, 0.01, 0.225, 0.25, 0.5);
    Chop._12.requireClose(correct, StaticHelperCausal.splits(mask));
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
