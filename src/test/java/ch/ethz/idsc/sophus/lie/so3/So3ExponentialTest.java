// code by jph / ob
package ch.ethz.idsc.sophus.lie.so3;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.lie.Cross;
import ch.ethz.idsc.tensor.lie.MatrixExp;
import ch.ethz.idsc.tensor.lie.RotationMatrix;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class So3ExponentialTest extends TestCase {
  public void testSimple() {
    Tensor vector = Tensors.vector(.2, .3, -.4);
    Tensor m1 = So3Exponential.INSTANCE.exp(vector);
    Tensor m2 = So3Exponential.INSTANCE.exp(vector.negate());
    assertFalse(Chop._12.close(m1, IdentityMatrix.of(3)));
    Chop._12.requireClose(m1.dot(m2), IdentityMatrix.of(3));
  }

  public void testLog() {
    Tensor vector = Tensors.vector(.2, .3, -.4);
    Tensor matrix = So3Exponential.INSTANCE.exp(vector);
    Tensor result = So3Exponential.INSTANCE.log(matrix);
    assertEquals(result, vector);
  }

  public void testTranspose() {
    Tensor vector = Tensors.vector(Math.random(), Math.random(), -Math.random());
    Tensor m1 = So3Exponential.INSTANCE.exp(vector);
    Tensor m2 = Transpose.of(So3Exponential.INSTANCE.exp(vector));
    Chop._12.requireClose(IdentityMatrix.of(3), m1.dot(m2));
  }

  private static void checkDiff(Tensor c) {
    Tensor e = So3Exponential.INSTANCE.exp(c);
    assertTrue(Chop._14.close(e, MatrixExp.of(Cross.skew3(c))));
    assertTrue(Chop._14.close(e.dot(c), c));
  }

  public void testXY() {
    Tensor m22 = RotationMatrix.of(RealScalar.ONE);
    Tensor mat = So3Exponential.INSTANCE.exp(Tensors.vector(0, 0, 1));
    Tensor blu = Tensors.of( //
        mat.get(0).extract(0, 2), //
        mat.get(1).extract(0, 2));
    assertEquals(blu, m22);
  }

  public void testFormula() {
    checkDiff(Tensors.vector(-.2, .1, .3));
    checkDiff(Tensors.vector(-.5, -.1, .03));
    checkDiff(Tensors.vector(-.3, -.2, .1));
    checkDiff(Tensors.vector(-.3, -.2, -.3));
  }

  public void testRotZ() {
    Tensor matrix = So3Exponential.INSTANCE.exp(Tensors.vector(0, 0, 1));
    assertEquals(matrix.get(2, 0), RealScalar.ZERO);
    assertEquals(matrix.get(2, 1), RealScalar.ZERO);
    assertEquals(matrix.get(0, 2), RealScalar.ZERO);
    assertEquals(matrix.get(1, 2), RealScalar.ZERO);
    assertEquals(matrix.get(2, 2), RealScalar.ONE);
  }

  public void testPi() {
    Tensor matrix = So3Exponential.INSTANCE.exp(Tensors.vector(0, 0, Math.PI));
    Tensor expected = DiagonalMatrix.of(-1, -1, 1);
    assertTrue(Chop._14.close(matrix, expected));
  }

  public void testTwoPi() {
    Tensor matrix = So3Exponential.INSTANCE.exp(Tensors.vector(0, 0, 2 * Math.PI));
    Tensor expected = DiagonalMatrix.of(1, 1, 1);
    assertTrue(Chop._14.close(matrix, expected));
  }

  public void testLogEye() {
    Tensor matrix = IdentityMatrix.of(3);
    Tensor log = So3Exponential.INSTANCE.log(matrix);
    assertTrue(Chop.NONE.allZero(log));
  }

  public void testLog1() {
    Tensor vec = Tensors.vector(.3, .5, -0.4);
    Tensor matrix = So3Exponential.INSTANCE.exp(vec);
    Tensor lom = So3Exponential.logMatrix(matrix);
    Tensor log = So3Exponential.INSTANCE.log(matrix);
    assertTrue(Chop._14.close(vec, log));
    assertTrue(Chop._14.close(lom, Cross.skew3(vec)));
  }

  public void testLogEps() {
    double v = 0.25;
    Tensor log;
    do {
      v = v / 1.1;
      Tensor vec = Tensors.vector(v, v, v);
      Tensor matrix = So3Exponential.INSTANCE.exp(vec);
      {
        Tensor logM = So3Exponential.logMatrix(matrix);
        assertTrue(Chop._13.close(logM.negate(), Transpose.of(logM)));
      }
      log = So3Exponential.INSTANCE.log(matrix);
    } while (!Chop.NONE.allZero(log));
  }

  public void testLogEps2() {
    double eps = Double.MIN_VALUE; // 4.9e-324
    Tensor vec = Tensors.vector(eps, 0, 0);
    Tensor matrix = So3Exponential.INSTANCE.exp(vec);
    Tensor log = So3Exponential.INSTANCE.log(matrix);
    assertTrue(Chop._50.allZero(log));
  }
}
