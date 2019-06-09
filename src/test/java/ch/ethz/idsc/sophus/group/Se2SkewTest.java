// code by jph
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class Se2SkewTest extends TestCase {
  public void testSimple() {
    Tensor tensor = Se2Skew.FUNCTION.apply(Tensors.vector(1, 2, 3));
    assertEquals(tensor, Tensors.vector(3.1063722664539783, -1.2872554670920426));
  }
}
