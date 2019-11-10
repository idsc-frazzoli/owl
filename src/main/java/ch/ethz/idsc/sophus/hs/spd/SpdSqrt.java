// code by jph
package ch.ethz.idsc.sophus.hs.spd;

import java.io.Serializable;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.mat.Eigensystem;
import ch.ethz.idsc.tensor.sca.Sqrt;

/** Reference:
 * "Riemannian Geometric Statistics in Medical Image Analysis", 2020
 * Edited by Pennec, Sommer, Fletcher, p. 80 */
public class SpdSqrt implements Serializable {
  private final Tensor forward;
  private final Tensor inverse;

  /** @param matrix symmetric
   * @return
   * @throws Exception if matrix is not symmetric */
  public SpdSqrt(Tensor matrix) {
    Eigensystem eigensystem = Eigensystem.ofSymmetric(matrix);
    Tensor avec = eigensystem.vectors();
    Tensor ainv = Transpose.of(avec);
    Tensor sqrt = eigensystem.values().map(Sqrt.FUNCTION);
    forward = ainv.dot(sqrt.pmul(avec));
    inverse = ainv.dot(sqrt.map(Scalar::reciprocal).pmul(avec));
  }

  public Tensor forward() {
    return forward;
  }

  public Tensor inverse() {
    return inverse;
  }
}
