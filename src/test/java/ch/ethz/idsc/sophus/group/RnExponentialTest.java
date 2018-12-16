// code by jph
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.mat.HilbertMatrix;
import junit.framework.TestCase;

public class RnExponentialTest extends TestCase {
  public void testSimple() {
    Tensor matrix = HilbertMatrix.of(2, 3);
    assertEquals(RnExponential.INSTANCE.exp(matrix), matrix);
    assertEquals(RnExponential.INSTANCE.log(matrix), matrix);
  }
}
