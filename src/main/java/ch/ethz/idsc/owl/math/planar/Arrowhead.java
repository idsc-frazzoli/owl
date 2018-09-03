// code by jph
package ch.ethz.idsc.owl.math.planar;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** the arrowhead is a pointy triangle with the
 * tip at coordinate (1, 0) and mean (0, 0).
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Arrowheads.html">Arrowheads</a> */
public enum Arrowhead {
  ;
  private static final Scalar THIRD = RationalScalar.of(1, 3);
  private static final Tensor POLYGON = Tensors.matrix(new Scalar[][] { //
      { RealScalar.ONE, RealScalar.ZERO }, //
      { RationalScalar.HALF.negate(), THIRD }, //
      { RationalScalar.HALF.negate(), THIRD.negate() } //
  });

  /** @param scalar
   * @return arrowhead coordinates scaled by given scalar */
  public static Tensor of(Scalar scalar) {
    return POLYGON.multiply(scalar);
  }

  /** @param number
   * @return arrowhead coordinates scaled by given number */
  public static Tensor of(Number number) {
    return of(RealScalar.of(number));
  }
}
