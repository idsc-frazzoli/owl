// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;

/** cubic B-spline */
public enum BSpline4CurveSubdivision {
  ;
  private static final Scalar _2_3 = RationalScalar.of(2, 3);
  private static final Scalar _1_16 = RationalScalar.of(1, 16);

  /** geodesic split suggested by Dyn/Sharon 2014 p.16 who also show
   * that the scheme with this split has a contractivity factor of mu = 5/6
   * 
   * @param geodesicInterface
   * @return */
  public static CurveSubdivision of(GeodesicInterface geodesicInterface) {
    return new Split2LoDual3PointCurveSubdivision(geodesicInterface, _2_3, _1_16);
  }

  /***************************************************/
  // FIXME design not final
  public static Scalar MAGIC_C = RationalScalar.of(1, 6);

  public static CurveSubdivision split3(GeodesicInterface geodesicInterface) {
    return split3(geodesicInterface, MAGIC_C);
  }

  /** @param geodesicInterface
   * @param c values in the interval [1/6, 2/3] give the best results
   * @return */
  public static CurveSubdivision split3(GeodesicInterface geodesicInterface, Scalar c) {
    return new Split3Dual3PointCurveSubdivision(geodesicInterface, //
        RealScalar.of(5).divide(RealScalar.of(16).multiply(c.subtract(RealScalar.ONE))).add(RealScalar.ONE), //
        c.multiply(RealScalar.of(16)).reciprocal(), //
        c);
  }

  /***************************************************/
  private static final Scalar _1_11 = RationalScalar.of(1, 11);
  private static final Scalar _11_16 = RationalScalar.of(11, 16);

  public static CurveSubdivision split2(GeodesicInterface geodesicInterface) {
    return new Split2HiDual3PointCurveSubdivision(geodesicInterface, _1_11, _11_16);
  }
}
