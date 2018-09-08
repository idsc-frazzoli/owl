// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;

/** dual scheme
 * Hormann/Sabin 2008: A Family of Subdivision Schemes with Cubic Precision */
public enum HormannSabinCurveSubdivision {
  ;
  private static final Scalar OMEGA = RationalScalar.of(1, 32);

  /** @param geodesicInterface
   * @return three-point scheme */
  public static CurveSubdivision of(GeodesicInterface geodesicInterface) {
    Scalar omega = OMEGA;
    Scalar pq_f = RationalScalar.HALF.add(RealScalar.of(6).multiply(omega));
    Scalar qr_f = RealScalar.of(6).multiply(omega).negate();
    Scalar pqqf = RationalScalar.HALF;
    return new Dual3PointCurveSubdivision(geodesicInterface, pq_f, qr_f, pqqf);
  }
}
