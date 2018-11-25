// code by jph
// adapted from document by Tobias Ewald
package ch.ethz.idsc.owl.math.planar;

import java.util.Optional;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Hypot;
import ch.ethz.idsc.tensor.red.Norm;

/** The implementation supports the use of Quantity.
 * The signed curvature is a Quantity with negated unit,
 * because curvature is the reciprocal of the radius.
 * 
 * Example: if the vectors are specified in coordinates with Unit "m"
 * then the function outputs values with unit "m^-1". */
public enum SignedCurvature2D {
  ;
  /** @param a vector of length 2
   * @param b vector of length 2
   * @param c vector of length 2
   * @return inverse of radius of circle that interpolates the given points a, b, c,
   * or Optional.empty() if any two of the three points are identical */
  public static Optional<Scalar> of(Tensor a, Tensor b, Tensor c) {
    Tensor d_ab = b.subtract(a);
    Scalar v = d_ab.dot(Cross2D.of(c.subtract(b))).Get();
    Scalar w = d_ab.dot(c.subtract(a)).Get();
    Scalar n = Norm._2.between(c, b);
    Scalar den = Hypot.of(v, w).multiply(n);
    return Scalars.isZero(den) //
        ? Optional.empty()
        : Optional.of(v.add(v).divide(den)); // 2 * v / den == (v + v) / den
  }
}
