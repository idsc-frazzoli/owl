// code by jph
package ch.ethz.idsc.sophus.lie.st;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class StGroupTest extends TestCase {
  public void testSt1Inverse() {
    Tensor p = Tensors.fromString("{3, {6, 3}}");
    Tensor id = Tensors.fromString("{1, {0, 0}}");
    StGroupElement pE = StGroup.INSTANCE.element(p);
    StGroupElement inv = pE.inverse();
    assertEquals(inv.toTensor(), Tensors.fromString("{1/3, {-2, -1}}"));
    assertEquals(inv.combine(p), id);
  }

  public void testSt1Combine() {
    Tensor p = Tensors.fromString("{3, {6, 1}}");
    StGroupElement pE = StGroup.INSTANCE.element(p);
    Tensor q = Tensors.fromString("{2, {8, 5}}");
    assertEquals(pE.combine(q), Tensors.fromString("{6, {30, 16}}"));
  }
}
