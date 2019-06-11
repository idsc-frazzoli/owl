// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** dual scheme */
public class Split2LoDual3PointCurveSubdivision extends Dual3PointCurveSubdivision {
  /** @param geodesicInterface non-null
   * @param p_qr
   * @param q_r
   * @return */
  public static CurveSubdivision of(GeodesicInterface geodesicInterface, Scalar p_qr, Scalar q_r) {
    return new Split2LoDual3PointCurveSubdivision(geodesicInterface, p_qr, q_r);
  }

  // ---
  private final Scalar p_q;
  private final Scalar pq_r;
  private final Scalar q_r;
  private final Scalar p_qr;

  private Split2LoDual3PointCurveSubdivision(GeodesicInterface geodesicInterface, Scalar p_q, Scalar pq_r) {
    super(geodesicInterface);
    this.p_q = p_q;
    this.pq_r = pq_r;
    q_r = RealScalar.ONE.subtract(p_q);
    p_qr = RealScalar.ONE.subtract(pq_r);
  }

  @Override // from Dual3PointCurveSubdivision
  protected Tensor lo(Tensor p, Tensor q, Tensor r) {
    Tensor pq = geodesicInterface.split(p, q, p_q);
    return geodesicInterface.split(pq, r, pq_r);
  }

  @Override // from Dual3PointCurveSubdivision
  protected Tensor hi(Tensor p, Tensor q, Tensor r) {
    Tensor qr = geodesicInterface.split(q, r, q_r);
    return geodesicInterface.split(p, qr, p_qr);
  }
}
