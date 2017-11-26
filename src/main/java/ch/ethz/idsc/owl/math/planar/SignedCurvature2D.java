// code by jph
// adapted from document by Tobias Ewald
package ch.ethz.idsc.owl.math.planar;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Hypot;
import ch.ethz.idsc.tensor.red.Norm;

public enum SignedCurvature2D {
  ;
  private static final Scalar TWO = RealScalar.of(2);

  /** @param a
   * @param b
   * @param c
   * @return inverse of radius of circle that interpolates the given points a, b, c */
  public static Scalar of(Tensor a, Tensor b, Tensor c) {
    // System.out.println("length of tensors: "+ a.length()+", "+ b.length()+", "+c.length());
    Scalar v = b.subtract(a).dot(Cross2D.of(c.subtract(b))).Get();
    Scalar w = b.subtract(a).dot(c.subtract(a)).Get();
    Scalar n = Norm._2.between(c, b);
    Scalar den = Hypot.of(v, w).multiply(n);
    return TWO.multiply(v).divide(den);
  }
}
