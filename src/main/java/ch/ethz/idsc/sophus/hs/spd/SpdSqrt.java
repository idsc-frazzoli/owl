// code by jph
package ch.ethz.idsc.sophus.hs.spd;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.mat.Eigensystem;
import ch.ethz.idsc.tensor.sca.Sqrt;

/** Reference:
 * "Riemannian Geometric Statistics in Medical Image Analysis", 2020
 * Edited by Pennec, Sommer, Fletcher, p. 80 */
public enum SpdSqrt {
  ;
  /** @param matrix
   * @return */
  public static Tensor of(Tensor matrix) {
    Eigensystem eigensystem = Eigensystem.ofSymmetric(matrix);
    Tensor a = eigensystem.vectors();
    Tensor ainv = Transpose.of(a);
    return ainv.dot(eigensystem.values().map(Sqrt.FUNCTION).pmul(a));
  }
}
