// code by jph
package ch.ethz.idsc.owl.math.planar;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/Arrowheads.html">Arrowheads</a> */
public enum Arrowhead {
  ;
  // former coordinates { .3, 0 }, { -.1, -.1 }, { -.1, +.1 }
  private static final Scalar THIRD = RationalScalar.of(1, 3);
  private static final Tensor POLYGON = Tensors.matrix(new Scalar[][] { //
      { RealScalar.ONE, RealScalar.ZERO }, //
      { RationalScalar.HALF.negate(), THIRD }, //
      { RationalScalar.HALF.negate(), THIRD.negate() } //
  });

  /** @param scalar
   * @return */
  public static Tensor of(Scalar scalar) {
    return POLYGON.multiply(scalar);
  }

  /** @param number
   * @return */
  public static Tensor of(Number number) {
    return of(RealScalar.of(number));
  }
}
