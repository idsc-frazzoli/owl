// code by jph
package ch.ethz.idsc.owl.math;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.mat.OrthogonalMatrixQ;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.qty.Quaternion;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class QuaternionToRotationMatrixTest extends TestCase {
  private static final Tensor ID3 = IdentityMatrix.of(3);

  public void testSimple() {
    Tensor matrix = QuaternionToRotationMatrix.of(Tensors.vector(0.240810, -0.761102, -0.355923, -0.485854));
    assertTrue(OrthogonalMatrixQ.of(matrix));
  }

  public void testRandom() {
    Distribution distribution = NormalDistribution.standard();
    for (int index = 0; index < 100; ++index) {
      Tensor wxyz = RandomVariate.of(distribution, 4);
      Tensor matrix = QuaternionToRotationMatrix.of(wxyz);
      assertTrue(OrthogonalMatrixQ.of(matrix));
      Scalar scalar = Quaternion.of(wxyz.Get(0), wxyz.Get(1), wxyz.Get(2), wxyz.Get(3));
      Quaternion invers = (Quaternion) scalar.reciprocal();
      Tensor invmat = QuaternionToRotationMatrix.of(Tensors.of(invers.re(), invers.im(), invers.jm(), invers.km()));
      assertTrue(Chop._12.close(matrix.dot(invmat), ID3));
    }
  }
}
