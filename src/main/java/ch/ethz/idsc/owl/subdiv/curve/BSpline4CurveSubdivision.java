// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;

/** cubic B-spline */
public enum BSpline4CurveSubdivision {
  ;
  /** values in the interval [1/6, 2/3] give the best results */
  public static Scalar MAGIC_C = RationalScalar.of(1, 6);

  public static CurveSubdivision of(GeodesicInterface geodesicInterface) {
    return create(geodesicInterface, MAGIC_C);
  }

  public static CurveSubdivision create(GeodesicInterface geodesicInterface, Scalar c) {
    Scalar pq_f = RealScalar.of(5).divide(RealScalar.of(16).multiply(c.subtract(RealScalar.ONE))).add(RealScalar.ONE);
    Scalar qr_f = c.multiply(RealScalar.of(16)).reciprocal();
    Scalar pqqr = c;
    return new ThreePointCurveSubdivision(geodesicInterface, pq_f, qr_f, pqqr);
  }
}
