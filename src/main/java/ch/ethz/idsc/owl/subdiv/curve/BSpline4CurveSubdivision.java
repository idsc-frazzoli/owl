// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;

/** cubic B-spline */
public enum BSpline4CurveSubdivision {
  ;
  /** values in the interval [1/6, 2/3] give the best results */
  // FIXME design not final
  public static Scalar MAGIC_C = RationalScalar.of(1, 6);

  public static CurveSubdivision of(GeodesicInterface geodesicInterface) {
    return split3(geodesicInterface, MAGIC_C);
  }

  public static CurveSubdivision split3(GeodesicInterface geodesicInterface, Scalar c) {
    return new Split3Dual3PointCurveSubdivision(geodesicInterface, //
        RealScalar.of(5).divide(RealScalar.of(16).multiply(c.subtract(RealScalar.ONE))).add(RealScalar.ONE), //
        c.multiply(RealScalar.of(16)).reciprocal(), //
        c);
  }

  public static CurveSubdivision split2Lo(GeodesicInterface geodesicInterface) {
    return new Split2LoDual3PointCurveSubdivision(geodesicInterface, //
        RationalScalar.of(2, 3), RationalScalar.of(1, 16));
  }

  public static CurveSubdivision split2Hi(GeodesicInterface geodesicInterface) {
    return new Split2HiDual3PointCurveSubdivision(geodesicInterface, //
        RationalScalar.of(11, 16), RationalScalar.of(1, 11));
  }
}
