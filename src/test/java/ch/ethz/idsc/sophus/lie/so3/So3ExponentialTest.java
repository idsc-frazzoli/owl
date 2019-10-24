// code by jph / ob
package ch.ethz.idsc.sophus.lie.so3;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.lie.Cross;
import ch.ethz.idsc.tensor.lie.MatrixExp;
import ch.ethz.idsc.tensor.lie.QRDecomposition;
import ch.ethz.idsc.tensor.lie.RotationMatrix;
import ch.ethz.idsc.tensor.mat.Det;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.mat.LowerTriangularize;
import ch.ethz.idsc.tensor.mat.OrthogonalMatrixQ;
import ch.ethz.idsc.tensor.mat.Orthogonalize;
import ch.ethz.idsc.tensor.mat.UnitaryMatrixQ;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.red.Diagonal;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Decrement;
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
    Chop._14.requireClose(e, MatrixExp.of(Cross.skew3(c)));
    Chop._14.requireClose(e.dot(c), c);
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
    Chop._14.requireClose(matrix, expected);
  }

  public void testTwoPi() {
    Tensor matrix = So3Exponential.INSTANCE.exp(Tensors.vector(0, 0, 2 * Math.PI));
    Tensor expected = DiagonalMatrix.of(1, 1, 1);
    Chop._14.requireClose(matrix, expected);
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
    Chop._14.requireClose(vec, log);
    Chop._14.requireClose(lom, Cross.skew3(vec));
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
        Chop._13.requireClose(logM.negate(), Transpose.of(logM));
      }
      log = So3Exponential.INSTANCE.log(matrix);
    } while (!Chop._20.allZero(log));
  }

  public void testLogEps2() {
    double eps = Double.MIN_VALUE; // 4.9e-324
    Tensor vec = Tensors.vector(eps, 0, 0);
    Tensor matrix = So3Exponential.INSTANCE.exp(vec);
    Tensor log = So3Exponential.INSTANCE.log(matrix);
    assertTrue(Chop._50.allZero(log));
  }

  public void testRodriques() {
    Distribution distribution = NormalDistribution.standard();
    for (int count = 0; count < 20; ++count) {
      Tensor matrix = So3Exponential.INSTANCE.exp(RandomVariate.of(distribution, 3));
      assertTrue(OrthogonalMatrixQ.of(matrix));
      OrthogonalMatrixQ.require(matrix);
    }
  }

  private static QRDecomposition specialOps(Tensor A) {
    QRDecomposition qrDecomposition = QRDecomposition.of(A);
    Tensor Q = qrDecomposition.getQ();
    Tensor Qi = qrDecomposition.getInverseQ();
    Tensor R = qrDecomposition.getR();
    Chop._10.requireClose(Q.dot(R), A);
    Chop._10.requireClose(Q.dot(Qi), IdentityMatrix.of(A.length()));
    Scalar qrDet = Det.of(Q).multiply(Det.of(R));
    Chop._10.requireClose(qrDet, Det.of(A));
    Tensor lower = LowerTriangularize.of(R, -1);
    assertTrue(Chop.NONE.allZero(lower));
    Chop._10.requireClose(qrDet, qrDecomposition.det());
    return qrDecomposition;
  }

  public void testRandomOrthogonal() {
    Distribution distribution = NormalDistribution.of(0, 5);
    for (int count = 0; count < 5; ++count) {
      Tensor matrix = So3Exponential.INSTANCE.exp(RandomVariate.of(distribution, 3));
      specialOps(matrix);
      QRDecomposition qr = QRDecomposition.preserveOrientation(matrix);
      Chop._13.requireClose(qr.getR(), IdentityMatrix.of(3));
      Chop._12.requireClose(qr.getQ(), matrix);
    }
  }

  public void testRandomOrthogonal2() {
    Distribution distribution = NormalDistribution.of(0, 5);
    Distribution noise = UniformDistribution.of(-0.03, 0.03);
    for (int count = 0; count < 5; ++count) {
      Tensor matrix = So3Exponential.INSTANCE.exp(RandomVariate.of(distribution, 3)).add(RandomVariate.of(noise, 3, 3));
      specialOps(matrix);
      QRDecomposition qr = QRDecomposition.preserveOrientation(matrix);
      Scalar infNorm = Norm.INFINITY.ofVector(Diagonal.of(qr.getR()).map(Decrement.ONE));
      assertTrue(Scalars.lessThan(infNorm, RealScalar.of(.1)));
    }
  }

  public void testRodriguez() {
    Tensor vector = RandomVariate.of(NormalDistribution.standard(), 3);
    Tensor wedge = Cross.skew3(vector);
    Chop._13.requireClose(MatrixExp.of(wedge), So3Exponential.INSTANCE.exp(vector));
  }

  public void testRodriques2() {
    Distribution dis = NormalDistribution.standard();
    for (int c = 0; c < 20; ++c) {
      Tensor matrix = So3Exponential.INSTANCE.exp(RandomVariate.of(dis, 3));
      assertTrue(UnitaryMatrixQ.of(matrix));
    }
  }

  public void testOrthPassFormatFailEye() {
    Scalar one = RealScalar.ONE;
    Tensor eyestr = Tensors.matrix((i, j) -> i.equals(j) ? one : one.zero(), 3, 4);
    try {
      So3Exponential.logMatrix(eyestr);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testOrthPassFormatFail2() {
    Tensor matrix = RandomVariate.of(NormalDistribution.standard(), 3, 5);
    Tensor orthog = Orthogonalize.of(matrix);
    assertTrue(OrthogonalMatrixQ.of(orthog));
    try {
      So3Exponential.logMatrix(orthog);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testFail() {
    try {
      So3Exponential.INSTANCE.exp(RealScalar.ZERO);
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      So3Exponential.INSTANCE.exp(Tensors.vector(0, 0));
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      So3Exponential.INSTANCE.exp(Tensors.vector(0, 0, 0, 0));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testLogTrash() {
    Tensor matrix = RandomVariate.of(NormalDistribution.standard(), 3, 3);
    try {
      So3Exponential.INSTANCE.log(matrix);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testLogFail() {
    try {
      So3Exponential.logMatrix(Array.zeros(3));
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      So3Exponential.logMatrix(Array.zeros(3, 4));
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      So3Exponential.logMatrix(Array.zeros(3, 3, 3));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
