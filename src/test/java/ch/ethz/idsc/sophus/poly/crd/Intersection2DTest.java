// code by jph
package ch.ethz.idsc.sophus.poly.crd;

import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class Intersection2DTest extends TestCase {
  public void testOrthogonal() {
    Tensor tensor = Intersection2D.of(Tensors.vector(-10, 0), Tensors.vector(1, 0), Tensors.vector(2, -1), Tensors.vector(0, 1));
    assertEquals(tensor, Tensors.vector(2, 0));
    ExactTensorQ.require(tensor);
  }

  public void testParallel() {
    try {
      Intersection2D.of(Tensors.vector(0, 0), Tensors.vector(1, 0), Tensors.vector(0, 1), Tensors.vector(1, 0));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testIdentical() {
    try {
      Intersection2D.of(Tensors.vector(0, 0), Tensors.vector(1, 0), Tensors.vector(0, 0), Tensors.vector(1, 0));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
