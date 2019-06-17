// code by jph
package ch.ethz.idsc.sophus.lie.he;

import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.HilbertMatrix;
import junit.framework.TestCase;

public class HeGroupElementTest extends TestCase {
  public void testInverse() {
    Tensor et = Tensors.fromString("{{0, 0}, {0, 0}, 0}");
    Tensor at = Tensors.fromString("{{1, 2}, {3, 4}, 5}");
    HeGroupElement a = new HeGroupElement(at);
    HeGroupElement b = a.inverse();
    Tensor result = b.combine(at);
    assertEquals(result, et);
  }

  public void testCombine() {
    Tensor a_t = Tensors.fromString("{{1, 2}, {3, 4}, 5}");
    HeGroupElement a = new HeGroupElement(a_t);
    Tensor b_t = Tensors.fromString("{{6, 7}, {8, 9}, 10}");
    Tensor ab_t = a.combine(b_t);
    ExactTensorQ.require(ab_t);
    assertEquals(ab_t, Tensors.fromString("{{7, 9}, {11, 13}, 41}"));
    HeGroupElement ab = new HeGroupElement(ab_t);
    Tensor a_r = ab.combine(new HeGroupElement(b_t).inverse().toTensor());
    assertEquals(a_r, a_t);
    Tensor b_r = a.inverse().combine(ab.toTensor());
    assertEquals(b_t, b_r);
  }

  public void testAdjoint1() {
    Tensor a_t = Tensors.fromString("{{1, 2}, {3, 4}, 5}");
    Tensor b_t = Tensors.fromString("{{6, 7}, {0, 0}, 10}");
    HeGroupElement a = new HeGroupElement(a_t);
    Tensor tensor = a.adjoint(b_t);
    assertEquals(tensor, Tensors.fromString("{{6, 7}, {0, 0}, -3*6-4*7+10}"));
    ExactTensorQ.require(tensor);
  }

  public void testAdjoint2() {
    Tensor a_t = Tensors.fromString("{{1, 2}, {3, 4}, 5}");
    Tensor b_t = Tensors.fromString("{{0, 0}, {6, 7}, 9}");
    HeGroupElement a = new HeGroupElement(a_t);
    Tensor tensor = a.adjoint(b_t);
    assertEquals(tensor, Tensors.fromString("{{0, 0}, {6, 7}, 1*6+2*7+9}"));
    ExactTensorQ.require(tensor);
  }

  public void testFail() {
    try {
      new HeGroupElement(Tensors.of(HilbertMatrix.of(3), Tensors.vector(1, 2, 3), RealScalar.ONE));
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      new HeGroupElement(Tensors.of(Tensors.vector(1, 2, 3), HilbertMatrix.of(3), RealScalar.ONE));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
