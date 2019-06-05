// code by jph
package ch.ethz.idsc.owl.math.order;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.HilbertMatrix;
import junit.framework.TestCase;

public class VectorLexicographicTest extends TestCase {
  public void testSimple1() {
    int c1 = VectorLexicographic.COMPARATOR.compare(Tensors.vector(0, 1, 2, 3), Tensors.vector(3, 0, 1, 2));
    int c2 = Integer.compare(0, 3);
    assertEquals(c1, -1);
    assertEquals(c1, c2);
  }

  public void testSimple2() {
    int c1 = VectorLexicographic.COMPARATOR.compare(Tensors.vector(0, 1, 2, 3), Tensors.vector(0, 1, 2, 2));
    int c2 = Integer.compare(3, 2);
    assertEquals(c1, c2);
  }

  public void testSimple3() {
    int c1 = VectorLexicographic.COMPARATOR.compare(Tensors.vector(0, 1, 2, 3), Tensors.vector(0, 1, 2, 4));
    int c2 = Integer.compare(3, 4);
    assertEquals(c1, c2);
  }

  public void testSimple4() {
    int c1 = VectorLexicographic.COMPARATOR.compare(Tensors.vector(0, 1, 2, 3), Tensors.vector(0, 1, 2, 3));
    int c2 = Integer.compare(3, 3);
    assertEquals(c1, c2);
    assertEquals(c1, 0);
  }

  public void testLengthFail() {
    Tensor x = Tensors.vector(0, 1, 2, 3);
    Tensor y = Tensors.vector(0, 1, 2, 4, 2);
    try {
      VectorLexicographic.COMPARATOR.compare(x, y);
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      VectorLexicographic.COMPARATOR.compare(y, x);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testMatrixFail() {
    try {
      VectorLexicographic.COMPARATOR.compare(HilbertMatrix.of(3), HilbertMatrix.of(3));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testScalarFail() {
    try {
      VectorLexicographic.COMPARATOR.compare(RealScalar.ONE, RealScalar.ZERO);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
