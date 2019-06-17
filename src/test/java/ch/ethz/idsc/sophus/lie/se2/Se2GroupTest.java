// code by jph
package ch.ethz.idsc.sophus.lie.se2;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class Se2GroupTest extends TestCase {
  public void testSimple() {
    Se2GroupElement se2GroupElement = Se2Group.INSTANCE.element(Tensors.vector(1, 2, 2 * Math.PI + 3));
    Tensor tensor = se2GroupElement.combine(Tensors.vector(0, 0, 0));
    assertEquals(tensor, Tensors.vector(1, 2, 3));
  }
}
