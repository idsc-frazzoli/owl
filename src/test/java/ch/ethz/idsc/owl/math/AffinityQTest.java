// code by jph
package ch.ethz.idsc.owl.math;

import ch.ethz.idsc.sophus.math.AffinityQ;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class AffinityQTest extends TestCase {
  public void testSimple() {
    AffinityQ.requirePositive(Tensors.vector(1, 0));
  }

  public void testFail() {
    try {
      AffinityQ.requirePositive(Tensors.vector(2, -1));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testFail2() {
    try {
      AffinityQ.requirePositive(Tensors.vector(1, 1));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
