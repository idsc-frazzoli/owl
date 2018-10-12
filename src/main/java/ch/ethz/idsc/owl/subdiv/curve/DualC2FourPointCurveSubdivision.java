// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.owl.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;

/** Nira Dyn, Michael S. Floater, Kai Hormann
 * A C2 Four-Point Subdivision Scheme with Fourth Order Accuracy and its Extensions */
public enum DualC2FourPointCurveSubdivision {
  ;
  public static CurveSubdivision cubic(GeodesicInterface geodesicInterface) {
    return of(geodesicInterface, RationalScalar.of(1, 128));
  }

  public static CurveSubdivision tightest(GeodesicInterface geodesicInterface) {
    return of(geodesicInterface, RealScalar.of(0.013723));
  }

  /** @param geodesicInterface
   * @param omega tension parameter
   * @return */
  public static CurveSubdivision of(GeodesicInterface geodesicInterface, Scalar omega) {
    Scalar pq_f = RealScalar.of(3).add(RealScalar.of(36).multiply(omega)) //
        .divide(RealScalar.of(3).add(RealScalar.of(8).multiply(omega)));
    Scalar rs_f = RealScalar.of(20).multiply(omega) //
        .divide(RealScalar.of(-1).add(RealScalar.of(8).multiply(omega)));
    Scalar pqrs = RealScalar.of(1).subtract(RealScalar.of(8).multiply(omega)) //
        .divide(RealScalar.of(4));
    return new Dual4PointCurveSubdivision(geodesicInterface, pq_f, rs_f, pqrs);
  }
}
