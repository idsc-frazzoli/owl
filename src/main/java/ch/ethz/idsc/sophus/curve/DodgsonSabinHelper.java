// code by jph
package ch.ethz.idsc.sophus.curve;

import ch.ethz.idsc.owl.math.planar.Cross2D;
import ch.ethz.idsc.owl.math.planar.SignedCurvature2D;
import ch.ethz.idsc.sophus.group.RnGeodesic;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.red.Norm2Squared;
import ch.ethz.idsc.tensor.red.Times;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.sca.Sqrt;

/* package */ enum DodgsonSabinHelper {
  ;
  static final CurveSubdivision BSPLINE3_EUCLIDEAN = new BSpline3CurveSubdivision(RnGeodesic.INSTANCE);
  private static final Scalar TWO = RealScalar.of(2);
  private static final Scalar _1_4 = RationalScalar.of(1, 4);

  /** @param b
   * @param c
   * @param d
   * @return point between b and c */
  static Tensor midpoint(Tensor b, Tensor c, Tensor d) {
    Scalar r = SignedCurvature2D.of(b, c, d).get();
    return intersectCircleLine(b, c, r, RealScalar.ZERO);
  }

  // not covered by tests
  static Tensor midpoint(Tensor b, Tensor c) {
    return b.add(c).multiply(RationalScalar.HALF);
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

  static Scalar lambda(Tensor a, Tensor b, Tensor c, Tensor d, Scalar r) {
    Scalar ac = Norm._2.between(a, c);
    Scalar bd = Norm._2.between(b, d);
    Scalar bc = Norm2Squared.between(b, c); // squared
    Scalar mu = ac.divide(bd);
    Scalar res = Times.of(r, r, bc, _1_4);
    Scalar h = res.divide(RealScalar.ONE.add(Sqrt.of(RealScalar.ONE.subtract(res))));
    Scalar mu1 = mu.add(RealScalar.ONE);
    Scalar mu1_2 = mu1.multiply(mu1);
    // (1 - h * 2 * mu / mu1_2)
    Scalar den = Times.of(TWO, h, mu).divide(mu1_2);
    return mu.subtract(RealScalar.ONE).divide(mu.add(RealScalar.ONE)).divide(RealScalar.ONE.subtract(den));
  }

  static Scalar averageCurvature(Tensor a, Tensor b, Tensor c, Tensor d) {
    Scalar ac = Norm._2.between(a, c);
    Scalar bd = Norm._2.between(b, d);
    return SignedCurvature2D.of(a, b, c).get().multiply(bd).add( //
        SignedCurvature2D.of(b, c, d).get().multiply(ac)) //
        .divide(bd.add(ac));
  }
}
