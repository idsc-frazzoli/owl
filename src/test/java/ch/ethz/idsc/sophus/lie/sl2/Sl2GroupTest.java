// code by jph
package ch.ethz.idsc.sophus.lie.sl2;

import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class Sl2GroupTest extends TestCase {
  public void testSimple() {
    Sl2GroupElement sl2GroupElement = Sl2Group.INSTANCE.element(Tensors.vector(8, 64, 4));
    Tensor inverse = sl2GroupElement.inverse().vector();
    ExactTensorQ.require(inverse);
    assertEquals(inverse, Tensors.vector(-2, -16, 0.25));
  }
}
