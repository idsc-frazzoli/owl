// code by jph
// adapted from document by Tobias Ewald
package ch.ethz.idsc.owl.math.planar;

import java.util.Optional;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Hypot;
import ch.ethz.idsc.tensor.red.Norm;

public enum SignedCurvature2D {
  ;
  /** @param a
   * @param b
   * @param c
   * @return inverse of radius of circle that interpolates the given points a, b, c,
   * or Optional.empty() if any two of the three points are identical */
  public static Optional<Scalar> of(Tensor a, Tensor b, Tensor c) {
    Scalar v = b.subtract(a).dot(Cross2D.of(c.subtract(b))).Get();
    Scalar w = b.subtract(a).dot(c.subtract(a)).Get();
    Scalar n = Norm._2.between(c, b);
    Scalar den = Hypot.of(v, w).multiply(n);
    return Scalars.isZero(den) //
        ? Optional.empty()
        : Optional.of(v.add(v).divide(den)); // 2 * v / den == (v + v) / den
  }
}
