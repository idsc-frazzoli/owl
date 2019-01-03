// code by jph
package ch.ethz.idsc.sophus.curve;

import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;

/** Nira Dyn, Michael S. Floater, Kai Hormann
 * A C2 Four-Point Subdivision Scheme with Fourth Order Accuracy and its Extensions
 * 
 * "One can show that the scheme has C2 continuity for w in the range of (0, 1/48]" */
public enum DualC2FourPointCurveSubdivision {
  ;
  /** "By viewing the scheme as a perturbation of Chaikin’s scheme [2], we can
   * easily introduce a tension parameter w, giving the extended scheme with
   * w=0 corresponding to the Chaikin scheme and w=1/128 corresponding to the
   * new four-point scheme (1)."
   * 
   * @param geodesicInterface
   * @return */
  public static CurveSubdivision cubic(GeodesicInterface geodesicInterface) {
    return of(geodesicInterface, RationalScalar.of(1, 128));
  }

  /** "The global minimum of T(w) is obtained for w=0.013723..., giving a C2
   * scheme that we call the tight four-point scheme. Like the scheme (1), it has
   * support size 7, but its accuracy is only O(h^2) as for Chaikin’s scheme."
   * 
   * @param geodesicInterface
   * @return */
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
