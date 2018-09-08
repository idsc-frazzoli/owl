// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;

/** cubic B-spline */
public class BSpline4CurveSubdivision extends Dual3PointCurveSubdivision {
  /** values in the interval [1/6, 2/3] give the best results */
  // FIXME design not final
  public static Scalar MAGIC_C = RationalScalar.of(1, 6);

  public static CurveSubdivision of(GeodesicInterface geodesicInterface) {
    return new BSpline4CurveSubdivision(geodesicInterface, MAGIC_C);
  }

  public BSpline4CurveSubdivision(GeodesicInterface geodesicInterface, Scalar c) {
    super(geodesicInterface, //
        RealScalar.of(5).divide(RealScalar.of(16).multiply(c.subtract(RealScalar.ONE))).add(RealScalar.ONE), //
        c.multiply(RealScalar.of(16)).reciprocal(), //
        c);
  }
}
