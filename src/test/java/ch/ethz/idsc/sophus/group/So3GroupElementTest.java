// code by jph
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.tensor.mat.HilbertMatrix;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import junit.framework.TestCase;

public class So3GroupElementTest extends TestCase {
  public void testSimple() {
    So3GroupElement so3GroupElement = So3GroupElement.of(IdentityMatrix.of(3));
    so3GroupElement.inverse();
    try {
      so3GroupElement.combine(HilbertMatrix.of(3));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testFail() {
    try {
      So3GroupElement.of(HilbertMatrix.of(3));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
