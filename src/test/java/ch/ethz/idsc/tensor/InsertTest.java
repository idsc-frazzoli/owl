// code by jph
package ch.ethz.idsc.tensor;

import junit.framework.TestCase;

public class InsertTest extends TestCase {
  public void testIndex0() {
    Tensor tensor = Tensors.fromString("{{1},{2},{3,4},5,{}}");
    Insert.inplace(tensor, Tensors.fromString("{{{9}}}"), 0);
    assertEquals(tensor, Tensors.fromString("{{{{9}}}, {1}, {2}, {3, 4}, 5, {}}"));
  }

  public void testIndex1() {
    Tensor tensor = Tensors.fromString("{{1},{2},{3,4},5,{}}");
    Insert.inplace(tensor, Tensors.fromString("{{{9}}}"), 1);
    assertEquals(tensor, Tensors.fromString("{{1}, {{{9}}}, {2}, {3, 4}, 5, {}}"));
  }

  public void testIndexLast() {
    Tensor tensor = Tensors.fromString("{{1},{2},{3,4},5,{}}");
    Insert.inplace(tensor, Tensors.fromString("{{{9}}}"), 5);
    assertEquals(tensor, Tensors.fromString("{{1}, {2}, {3, 4}, 5, {}, {{{9}}}}"));
  }

  public void testFailSmall() {
    Insert.inplace(Tensors.vector(1, 2, 3), RealScalar.ZERO, 0);
    try {
      Insert.inplace(Tensors.vector(1, 2, 3), RealScalar.ZERO, -1);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testFailLarge() {
    Insert.inplace(Tensors.vector(1, 2, 3), RealScalar.ZERO, 3);
    try {
      Insert.inplace(Tensors.vector(1, 2, 3), RealScalar.ZERO, 4);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
