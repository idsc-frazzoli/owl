// code by jph
package ch.ethz.idsc.sophus.math;

import java.util.Random;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.Det;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.mat.OrthogonalMatrixQ;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.qty.Quaternion;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Conjugate;
import junit.framework.TestCase;

public class QuaternionToRotationMatrixTest extends TestCase {
  private static final Tensor ID3 = IdentityMatrix.of(3);

  public void testSimple() {
    Tensor matrix = QuaternionToRotationMatrix.of(Tensors.vector(0.240810, -0.761102, -0.355923, -0.485854));
    assertTrue(OrthogonalMatrixQ.of(matrix));
    Chop._12.requireClose(Det.of(matrix), RealScalar.ONE);
  }

  public void testRandom() {
    Distribution distribution = NormalDistribution.standard();
    for (int index = 0; index < 100; ++index) {
      Tensor wxyz = RandomVariate.of(distribution, 4);
      Tensor matrix = QuaternionToRotationMatrix.of(wxyz);
      assertTrue(OrthogonalMatrixQ.of(matrix, Chop._12));
      Chop._12.requireClose(Det.of(matrix), RealScalar.ONE);
      Scalar scalar = Quaternion.of(wxyz.Get(0), wxyz.Get(1), wxyz.Get(2), wxyz.Get(3));
      Quaternion invers = (Quaternion) scalar.reciprocal();
      Tensor invmat = QuaternionToRotationMatrix.of(Tensors.of(invers.re(), invers.im(), invers.jm(), invers.km()));
      assertTrue(Chop._12.close(matrix.dot(invmat), ID3));
    }
  }

  public void testQuaternionVector() {
    Random random = new Random();
    for (int index = 0; index < 100; ++index) {
      Scalar q = Quaternion.of(random.nextGaussian(), random.nextGaussian(), random.nextGaussian(), random.nextGaussian());
      q = q.divide(q.abs()); // normalize
      Tensor vector = RandomVariate.of(NormalDistribution.standard(), 3);
      Scalar v = Quaternion.of(RealScalar.ZERO, vector.Get(0), vector.Get(1), vector.Get(2));
      Quaternion qvq = (Quaternion) q.multiply(v).multiply(Conjugate.FUNCTION.apply(q));
      Quaternion qq = (Quaternion) q;
      Tensor matrix = QuaternionToRotationMatrix.of(Tensors.of(qq.re(), qq.im(), qq.jm(), qq.km()));
      assertTrue(OrthogonalMatrixQ.of(matrix, Chop._12));
      Chop._12.requireClose(Det.of(matrix), RealScalar.ONE);
      Tensor result = matrix.dot(vector);
      assertTrue(Chop._12.close(result, Tensors.of(qvq.im(), qvq.jm(), qvq.km())));
    }
  }
}
