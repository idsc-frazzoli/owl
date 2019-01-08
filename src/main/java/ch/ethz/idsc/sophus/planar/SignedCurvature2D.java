// code by jph
// adapted from document by Tobias Ewald
package ch.ethz.idsc.sophus.planar;

import java.util.Optional;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
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

  /** @param points of the form {{p1x, p1y}, {p2x, p2y}, ..., {pNx, pNy}}
   * @return vector */
  public static Tensor string(Tensor points) {
    int length = points.length();
    Tensor vector = Array.zeros(length);
    for (int index = 1; index < points.length() - 1; ++index)
      vector.set(of( //
          points.get(index - 1), //
          points.get(index + 0), //
          points.get(index + 1) //
      ).orElse(RealScalar.ZERO), index);
    if (2 < length) {
      vector.set(vector.get(1), 0);
      vector.set(vector.get(length - 2), length - 1);
    }
    return vector;
  }
}
