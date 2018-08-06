// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;

/** cubic B-spline */
public enum BSpline4CurveSubdivision {
  ;
  private static final Scalar OMEGA = RationalScalar.of(-1, 48);

  // TODO JAN consider changing API [for instance bspline2 == FPS(0) and yet no abstraction]
  public static CurveSubdivision of(GeodesicInterface geodesicInterface) {
    return new ThreePointCurveSubdivision(geodesicInterface, OMEGA);
  }
}
