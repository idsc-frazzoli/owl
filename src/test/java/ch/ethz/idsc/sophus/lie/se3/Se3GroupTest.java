// code by jph
package ch.ethz.idsc.sophus.lie.se3;

import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.mat.HilbertMatrix;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import junit.framework.TestCase;

public class Se3GroupTest extends TestCase {
  public void testSimple() {
    Se3Group.INSTANCE.element(IdentityMatrix.of(4));
  }

  public void testVectorFail() {
    try {
      Se3Group.INSTANCE.element(UnitVector.of(4, 1));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testMatrixFail() {
    try {
      Se3Group.INSTANCE.element(HilbertMatrix.of(3));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
