// code by ob, jph
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Tan;

/* package */ enum So2Skew {
  ;
  public static Tensor of(Scalar angle) {
    return of(angle, RealScalar.ONE);
  }

  /** @param angle
   * @return matrix of dimensions 2 x 2 */
  public static Tensor of(Scalar angle, Scalar weight) {
    if (Scalars.isZero(angle))
      return Tensors.matrix(new Scalar[][] { //
          { weight, weight.zero() }, //
          { weight.zero(), weight } });
    // ---
    Scalar angle_half = angle.multiply(RationalScalar.HALF);
    Scalar m12 = angle_half.multiply(weight);
    Scalar m11 = m12.divide(Tan.FUNCTION.apply(angle_half));
    return Tensors.matrix(new Scalar[][] { //
        { m11, m12 }, //
        { m12.negate(), m11 } });
  }
}
