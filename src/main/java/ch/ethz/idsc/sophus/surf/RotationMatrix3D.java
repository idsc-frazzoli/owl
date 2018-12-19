// code by jph
package ch.ethz.idsc.sophus.surf;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.lie.Cross;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;

/** formula taken from Ethan Eade:
 * Rotation Between Two Vectors in R^3 */
public enum RotationMatrix3D {
  ;
  private static final Tensor ID3 = IdentityMatrix.of(3);

  /** @param a vector with 3 entries and 2-norm equals to 1
   * @param b vector with 3 entries and 2-norm equals to 1
   * @return 3x3 orthogonal matrix */
  public static Tensor of(Tensor a, Tensor b) {
    Tensor w = Cross.of(a, b);
    Tensor wx = Cross.of(w);
    Scalar ab = a.dot(b).Get();
    Scalar c = ab.add(RealScalar.ONE).reciprocal();
    return ID3.add(wx).add(wx.dot(wx).multiply(c));
  }
}
