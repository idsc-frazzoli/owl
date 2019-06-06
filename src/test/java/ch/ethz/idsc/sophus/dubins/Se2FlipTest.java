// code by jph
package ch.ethz.idsc.sophus.dubins;

import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class Se2FlipTest extends TestCase {
  public void testSimple() {
    Tensor tensor = Se2Flip.FUNCTION.apply(Tensors.vector(1, 2, 3));
    ExactTensorQ.require(tensor);
    assertEquals(tensor, Tensors.vector(1, -2, -3));
  }
}
