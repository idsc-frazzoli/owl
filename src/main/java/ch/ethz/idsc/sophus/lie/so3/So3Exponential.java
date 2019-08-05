// code by jph
package ch.ethz.idsc.sophus.lie.so3;

import ch.ethz.idsc.sophus.lie.LieExponential;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.lie.Cross;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.mat.OrthogonalMatrixQ;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.ArcCos;
import ch.ethz.idsc.tensor.sca.Sinc;
import ch.ethz.idsc.tensor.sca.Sqrt;

/** a group element SO(3) is represented as a 3x3 orthogonal matrix.
 * an element of the algebra so(3) is represented as a vector of length 3
 * 
 * <p>Olinde Rodrigues' formula is a fast and robust method to
 * compute the exponential of a skew symmetric 3x3 matrix.
 * formula taken from Blanes/Casas
 * "A concise introduction to geometric numerical integration"
 * p. 131
 * 
 * <p>The formula for the logarithm is taken from a book by Chirikjian */
public enum So3Exponential implements LieExponential {
  INSTANCE;
  // ---
  private static final Tensor ID3 = IdentityMatrix.of(3);
  private static final Scalar HALF = DoubleScalar.of(0.5);

  @Override // from LieExponential
  public Tensor exp(Tensor vector) {
    Scalar beta = Norm._2.ofVector(vector);
    Scalar s1 = Sinc.FUNCTION.apply(beta);
    Tensor X1 = Cross.skew3(vector.multiply(s1));
    Scalar h2 = Sinc.FUNCTION.apply(beta.multiply(HALF));
    Scalar r2 = Sqrt.FUNCTION.apply(h2.multiply(h2).multiply(HALF));
    Tensor X2 = Cross.skew3(vector.multiply(r2));
    return ID3.add(X1).add(X2.dot(X2));
  }

  /** @param matrix with dimensions 3 x 3 that satisfies OrthogonalMatrixQ
   * @return skew-symmetric 3 x 3 matrix X with exp X == matrix */
  public static Tensor logMatrix(Tensor matrix) {
    if (OrthogonalMatrixQ.of(matrix)) {
      Scalar sinc = Sinc.FUNCTION.apply(theta(matrix));
      return matrix.subtract(Transpose.of(matrix)).divide(sinc.add(sinc));
    }
    throw TensorRuntimeException.of(matrix);
  }

  @Override // from LieExponential
  public Tensor log(Tensor matrix) {
    Tensor log = logMatrix(matrix);
    return Tensors.of(log.Get(2, 1), log.Get(0, 2), log.Get(1, 0));
  }

  private static Scalar theta(Tensor matrix) {
    Scalar value = matrix.Get(0, 0).add(matrix.Get(1, 1)).add(matrix.Get(2, 2)) //
        .subtract(RealScalar.ONE).multiply(HALF);
    return ArcCos.FUNCTION.apply(value);
  }
}
