// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;

/** cubic B-spline */
public enum BSpline6CurveSubdivision {
  ;
  private static final Scalar _5_6 = RationalScalar.of(5, 6);
  private static final Scalar _1_22 = RationalScalar.of(1, 22);
  private static final Scalar _11_32 = RationalScalar.of(11, 32);

  public static CurveSubdivision of(GeodesicInterface geodesicInterface) {
    return new Dual4PointCurveSubdivision(geodesicInterface, _5_6, _1_22, _11_32);
  }
}
