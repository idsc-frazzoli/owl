// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import ch.ethz.idsc.sophus.math.SplitInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** dual scheme */
public class Split2HiDual3PointCurveSubdivision extends Dual3PointCurveSubdivision {
  /** @param geodesicInterface non-null
   * @param p_qr
   * @param q_r
   * @return */
  public static CurveSubdivision of(SplitInterface splitInterface, Scalar p_qr, Scalar q_r) {
    return new Split2HiDual3PointCurveSubdivision(splitInterface, p_qr, q_r);
  }

  // ---
  private final Scalar q_r;
  private final Scalar p_qr;
  private final Scalar p_q;
  private final Scalar pq_r;

  private Split2HiDual3PointCurveSubdivision(SplitInterface splitInterface, Scalar p_qr, Scalar q_r) {
    super(splitInterface);
    this.q_r = q_r;
    this.p_qr = p_qr;
    p_q = RealScalar.ONE.subtract(q_r);
    pq_r = RealScalar.ONE.subtract(p_qr);
  }

  @Override // from Dual3PointCurveSubdivision
  protected Tensor lo(Tensor p, Tensor q, Tensor r) {
    Tensor qr = splitInterface.split(q, r, q_r);
    return splitInterface.split(p, qr, p_qr);
  }

  @Override
  protected Tensor hi(Tensor p, Tensor q, Tensor r) {
    Tensor pq = splitInterface.split(p, q, p_q);
    return splitInterface.split(pq, r, pq_r);
  }
}
