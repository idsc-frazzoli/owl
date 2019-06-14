// code by ob, jph
package ch.ethz.idsc.sophus.lie.so2c;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Tan;

/** Reference:
 * "Exponential Barycenters of the Canonical Cartan Connection and Invariant Means on Lie Groups"
 * by Xavier Pennec, Vincent Arsigny, p.35, Section 4.5
 * 
 * (1 - Cos[t]) / Sin[t] == Tan[t/2] */
public enum So2CoveringSkew {
  ;
  /** @param angle
   * @return matrix of dimensions 2 x 2 */
  public static Tensor of(Scalar angle) {
    return of(angle, RealScalar.ONE);
  }

  /** The determinant of the matrix is of the form
   * (t^2 Cos[t])/(2 - 2 Cos[t])
   * which evaluates to zero for t == pi/2 + z pi where z is any integer
   * 
   * @param angle
   * @param weight
   * @return matrix of dimensions 2 x 2 */
  public static Tensor of(Scalar angle, Scalar weight) {
    Scalar angle_half = angle.multiply(RationalScalar.HALF);
    Scalar den = Tan.FUNCTION.apply(angle_half);
    if (Scalars.isZero(den))
      return Tensors.matrix(new Scalar[][] { //
          { weight, weight.zero() }, //
          { weight.zero(), weight } });
    Scalar m12 = angle_half.multiply(weight);
    Scalar m11 = m12.divide(den);
    return Tensors.matrix(new Scalar[][] { //
        { m11, m12 }, //
        { m12.negate(), m11 } });
  }
}
