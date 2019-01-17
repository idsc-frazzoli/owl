// code by ob
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class St1GroupElementTest extends TestCase {
  public void testInverse() {
    Tensor p = Tensors.vector(3, 6);
    Tensor id = Tensors.vector(1, 0);
    St1GroupElement pE = new St1GroupElement(p);
    assertEquals(pE.inverse().combine(p), id);
  }

  public void testCombine() {
    Tensor p = Tensors.vector(3, 6);
    St1GroupElement pE = new St1GroupElement(p);
    Tensor q = Tensors.vector(2, 8);
    assertEquals(pE.combine(q), Tensors.vector(2 * 3, 3 * 8 + 6));
  }

  public void testFail() {
    try {
      new St1GroupElement(Tensors.vector(0, 5));
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      new St1GroupElement(Tensors.vector(-1, 5));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
