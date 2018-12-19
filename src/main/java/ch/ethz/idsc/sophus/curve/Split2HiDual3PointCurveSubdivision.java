// code by jph
package ch.ethz.idsc.sophus.curve;

import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** dual scheme */
public class Split2HiDual3PointCurveSubdivision extends Dual3PointCurveSubdivision {
  /** @param geodesicInterface non-null
   * @param p_qr
   * @param q_r
   * @return */
  public static CurveSubdivision of(GeodesicInterface geodesicInterface, Scalar p_qr, Scalar q_r) {
    return new Split2HiDual3PointCurveSubdivision(geodesicInterface, p_qr, q_r);
  }

  // ---
  private final Scalar q_r;
  private final Scalar p_qr;

  private Split2HiDual3PointCurveSubdivision(GeodesicInterface geodesicInterface, Scalar p_qr, Scalar q_r) {
    super(geodesicInterface);
    this.q_r = q_r;
    this.p_qr = p_qr;
  }

  @Override // from Dual3PointCurveSubdivision
  protected Tensor lo(Tensor p, Tensor q, Tensor r) {
    Tensor qr = geodesicInterface.split(q, r, q_r);
    return geodesicInterface.split(p, qr, p_qr);
  }
}
