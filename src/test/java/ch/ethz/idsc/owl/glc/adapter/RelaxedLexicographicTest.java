// code by ynager
package ch.ethz.idsc.owl.glc.adapter;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import junit.framework.TestCase;

public class RelaxedLexicographicTest extends TestCase {
  public void testOne() {
    Tensor a;
    Tensor b;
    int comp;
    // ---
    Tensor slacks = Tensors.vector(0.1, 0.1, 0.1);
    RelaxedLexicographic c = new RelaxedLexicographic(slacks);
    // ---
    a = Tensors.vector(111, 0, 0);
    b = Tensors.vector(100, 500, 500);
    comp = c.quasiCompare(a, b);
    assertEquals(comp, Integer.compare(1, 0));
    // ---
    a = Tensors.vector(110, 0, 0);
    b = Tensors.vector(100, 500, 500);
    comp = c.quasiCompare(a, b);
    assertEquals(comp, Integer.compare(0, 1));
    // ---
    a = Tensors.vector(110, 500, 500);
    b = Tensors.vector(100, 500, 500);
    comp = c.quasiCompare(a, b);
    assertEquals(comp, Integer.compare(0, 0));
    // ---
    a = Tensors.vector(96, 95, 111);
    b = Tensors.vector(100, 100, 100);
    comp = c.quasiCompare(a, b);
    assertEquals(comp, Integer.compare(1, 0));
    // ---
    a = Tensors.vector(100, 0, 0.1);
    b = Tensors.vector(100, 0, 0);
    comp = c.quasiCompare(a, b);
    assertEquals(comp, Integer.compare(1, 0));
  }

  public void testMore() {
    Tensor slacks = Tensors.vector(0.1, 0, 0.1);
    RelaxedLexicographic c = new RelaxedLexicographic(slacks);
    Tensor a = Tensors.vector(99, 100.01, 0);
    Tensor b = Tensors.vector(100, 100, 100);
    int comp = c.quasiCompare(a, b);
    assertEquals(comp, Integer.compare(1, 0));
    slacks = Tensors.vector(0, 0);
    c = new RelaxedLexicographic(slacks);
    a = Tensors.vector(7, 1.002);
    b = Tensors.vector(7, 1.001);
    comp = c.quasiCompare(a, b);
    assertEquals(comp, Integer.compare(1, 0));
  }

  public void testQuantity() {
    Tensor slacks = Tensors.fromString("{1/10[m],2/7[s]}");
    RelaxedLexicographic c = new RelaxedLexicographic(slacks);
    Tensor a = Tensors.fromString("{3[m],5[s]}");
    Tensor b = Tensors.fromString("{2[m],6[s]}");
    try { // TODO not yet compatible for quantity
      int comp = c.quasiCompare(a, b);
      assertEquals(comp, Integer.compare(1, 0));
      slacks = Tensors.vector(0, 0);
      c = new RelaxedLexicographic(slacks);
      a = Tensors.vector(7, 1.002);
      b = Tensors.vector(7, 1.001);
      comp = c.quasiCompare(a, b);
      assertEquals(comp, Integer.compare(1, 0));
    } catch (Exception exception) {
      // ---
    }
  }

  public void testFail() {
    try {
      new RelaxedLexicographic(IdentityMatrix.of(3));
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
  }
}
