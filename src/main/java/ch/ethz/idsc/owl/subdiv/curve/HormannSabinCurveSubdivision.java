// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;

/** dual scheme
 * 
 * Hormann/Sabin 2006/2008: A Family of Subdivision Schemes with Cubic Precision
 * Quote from the article p.3:
 * "This is the 'black sheep' of the family because it does not have cubic precision. [...]
 * It is similar to the dual four-point scheme, but instead of the two new points in each
 * span coming from a cubic through four points, the two new points adjacent to a given old
 * point are taken by sampling a quadratic through three adjacent old points [...] It
 * therefore has quadratic precision by construction." */
public enum HormannSabinCurveSubdivision {
  ;
  private static final Scalar P6_7 = RationalScalar.of(6, 7);
  private static final Scalar N3_32 = RationalScalar.of(-3, 32);

  public static CurveSubdivision of(GeodesicInterface geodesicInterface) {
    return new Split2LoDual3PointCurveSubdivision(geodesicInterface, P6_7, N3_32);
  }

  /***************************************************/
  private static final Scalar OMEGA = RationalScalar.of(1, 32);

  /** @param geodesicInterface
   * @return three-point scheme */
  public static CurveSubdivision split3(GeodesicInterface geodesicInterface) {
    Scalar omega = OMEGA;
    Scalar pq_f = RationalScalar.HALF.add(RealScalar.of(6).multiply(omega));
    Scalar qr_f = RealScalar.of(6).multiply(omega).negate();
    Scalar pqqf = RationalScalar.HALF;
    return new Split3Dual3PointCurveSubdivision(geodesicInterface, pq_f, qr_f, pqqf);
  }

  /***************************************************/
  private static final Scalar P27_32 = RationalScalar.of(27, 32);
  private static final Scalar N1_9 = RationalScalar.of(-1, 9);

  public static CurveSubdivision split2(GeodesicInterface geodesicInterface) {
    return new Split2HiDual3PointCurveSubdivision(geodesicInterface, P27_32, N1_9);
  }
}
