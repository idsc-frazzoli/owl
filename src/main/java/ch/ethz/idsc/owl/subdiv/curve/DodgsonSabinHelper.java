// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.owl.math.group.RnGeodesic;
import ch.ethz.idsc.owl.math.planar.Cross2D;
import ch.ethz.idsc.owl.math.planar.SignedCurvature2D;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.red.Norm2Squared;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.sca.Sqrt;

/* package */ enum DodgsonSabinHelper {
  ;
  static final CurveSubdivision BSPLINE3_EUCLIDEAN = new BSpline3CurveSubdivision(RnGeodesic.INSTANCE);

  /** @param b
   * @param c
   * @param d
   * @return point between b and c */
  static Tensor midpoint(Tensor b, Tensor c, Tensor d) {
    Scalar r = SignedCurvature2D.of(b, c, d).get();
    return intersectCircleLine(b, c, r, RealScalar.ZERO);
  }

  static Tensor midpoint(Tensor B, Tensor C) {
    return B.add(C).multiply(RationalScalar.HALF);
  }

  static Tensor midpoint(Tensor a, Tensor b, Tensor c, Tensor d) {
    Scalar R = averageCurvature(a, b, c, d);
    Scalar lam = lambda(a, b, c, d, R);
    return intersectCircleLine(b, c, R, lam);
  }

  static Tensor intersectCircleLine(Tensor b, Tensor c, Scalar r, Scalar lam) {
    Tensor D = c.subtract(b);
    double d = Norm2Squared.ofVector(D).number().doubleValue();
    double l2 = lam.multiply(lam).number().doubleValue();
    double R2 = r.multiply(r).number().doubleValue();
    double fa = 1 / (1 + Math.sqrt(1 - R2 * d * 0.25));
    double fb = l2 / (1 + Math.sqrt(1 - l2 * R2 * d * 0.25));
    return Total.of(Tensors.of( //
        b.add(c).multiply(RealScalar.of(0.5)), //
        D.multiply(lam.divide(RealScalar.of(2))), //
        Cross2D.of(D).multiply(RealScalar.of(r.number().doubleValue() * Math.sqrt(d) * 0.25 * (fa - fb)))));
  }

  static Scalar lambda(Tensor A, Tensor B, Tensor C, Tensor D, Scalar R) {
    Scalar a = Norm._2.between(D, B);
    Scalar b = Norm._2.between(C, A);
    Scalar d = Norm2Squared.between(C, B);
    Scalar mu = b.divide(a);
    Scalar res = R.multiply(R).multiply(d).divide(RealScalar.of(4));
    Scalar H = res.divide(RealScalar.ONE.add(Sqrt.of(RealScalar.ONE.subtract(res))));
    Scalar mu1 = mu.add(RealScalar.ONE);
    Scalar mu1_2 = mu1.multiply(mu1);
    // (1 - H * 2 * mu / mu1_2)
    Scalar den = H.multiply(RealScalar.of(2)).multiply(mu).divide(mu1_2);
    return mu.subtract(RealScalar.ONE).divide(mu.add(RealScalar.ONE)).divide(RealScalar.ONE.subtract(den));
  }

  static Scalar averageCurvature(Tensor A, Tensor B, Tensor C, Tensor D) {
    Scalar a = Norm._2.between(D, B);
    Scalar b = Norm._2.between(C, A);
    return SignedCurvature2D.of(A, B, C).get().multiply(a).add( //
        SignedCurvature2D.of(B, C, D).get().multiply(b)) //
        .divide(a.add(b));
  }
}
