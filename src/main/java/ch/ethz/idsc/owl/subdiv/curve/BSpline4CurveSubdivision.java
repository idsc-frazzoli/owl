// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;

/** cubic B-spline */
public enum BSpline4CurveSubdivision {
  ;
  private static final Scalar P2_3 = RationalScalar.of(2, 3);
  private static final Scalar P1_16 = RationalScalar.of(1, 16);

  /** geodesic split suggested by Dyn/Sharon 2014 p.16 who also show
   * that the scheme with this split has a contractivity factor of mu = 5/6
   * 
   * @param geodesicInterface
   * @return */
  public static CurveSubdivision of(GeodesicInterface geodesicInterface) {
    return new Split2LoDual3PointCurveSubdivision(geodesicInterface, P2_3, P1_16);
  }

  /** @param geodesicInterface
   * @param value in the interval [1/6, 2/3] give the best results
   * @return */
  public static CurveSubdivision split3(GeodesicInterface geodesicInterface, Scalar value) {
    return new Split3Dual3PointCurveSubdivision(geodesicInterface, //
        RealScalar.of(5).divide(RealScalar.of(16).multiply(value.subtract(RealScalar.ONE))).add(RealScalar.ONE), //
        value.multiply(RealScalar.of(16)).reciprocal(), //
        value);
  }

  /***************************************************/
  private static final Scalar P11_16 = RationalScalar.of(11, 16);
  private static final Scalar P1_11 = RationalScalar.of(1, 11);

  public static CurveSubdivision split2(GeodesicInterface geodesicInterface) {
    return new Split2HiDual3PointCurveSubdivision(geodesicInterface, P11_16, P1_11);
  }
}
