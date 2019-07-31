// code by jph
package ch.ethz.idsc.sophus.lie;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.lie.RotationMatrix;
import junit.framework.TestCase;

public class MatrixBracketTest extends TestCase {
  public void testSe2Matrix() {
    Tensor bx = Tensors.fromString("{{0, 0, 1}, {0, 0, 0}, {0, 0, 0}}");
    Tensor by = Tensors.fromString("{{0, 0, 0}, {0, 0, 1}, {0, 0, 0}}");
    Tensor bt = Tensors.fromString("{{0, -1, 0}, {1, 0, 0}, {0, 0, 0}}");
    assertEquals(MatrixBracket.of(bx, by), Array.zeros(3, 3));
    assertEquals(MatrixBracket.of(bt, bx), by);
    assertEquals(MatrixBracket.of(by, bt), bx);
  }

  public void testBracketVectorFail() {
    try {
      MatrixBracket.of(Tensors.empty(), Tensors.empty());
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      MatrixBracket.of(Tensors.vector(1, 2), Tensors.vector(3, 4));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testBracketMatrixFail() {
    Tensor x = RotationMatrix.of(RealScalar.ONE);
    Tensor y = Tensors.vector(3, 4);
    try {
      MatrixBracket.of(x, y);
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      MatrixBracket.of(y, x);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testBracketAdFail() {
    try {
      MatrixBracket.of(Array.zeros(2, 2, 2), Array.zeros(2, 2, 2));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testBracketAdVectorFail() {
    Tensor x = Array.zeros(3, 3, 3);
    Tensor y = Tensors.vector(1, 2, 3);
    try {
      MatrixBracket.of(x, y);
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      MatrixBracket.of(y, x);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
