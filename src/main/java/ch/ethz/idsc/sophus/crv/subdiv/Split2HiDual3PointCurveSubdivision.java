// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.BinaryAverage;

/** dual scheme */
public class Split2HiDual3PointCurveSubdivision extends Dual3PointCurveSubdivision {
  /** @param geodesicInterface non-null
   * @param p_qr
   * @param q_r
   * @return */
  public static CurveSubdivision of(BinaryAverage binaryAverage, Scalar p_qr, Scalar q_r) {
    return new Split2HiDual3PointCurveSubdivision(binaryAverage, p_qr, q_r);
  }

  // ---
  private final Scalar q_r;
  private final Scalar p_qr;
  private final Scalar p_q;
  private final Scalar pq_r;

  private Split2HiDual3PointCurveSubdivision(BinaryAverage binaryAverage, Scalar p_qr, Scalar q_r) {
    super(binaryAverage);
    this.q_r = q_r;
    this.p_qr = p_qr;
    p_q = RealScalar.ONE.subtract(q_r);
    pq_r = RealScalar.ONE.subtract(p_qr);
  }

  @Override // from Dual3PointCurveSubdivision
  protected Tensor lo(Tensor p, Tensor q, Tensor r) {
    Tensor qr = binaryAverage.split(q, r, q_r);
    return binaryAverage.split(p, qr, p_qr);
  }

  @Override
  protected Tensor hi(Tensor p, Tensor q, Tensor r) {
    Tensor pq = binaryAverage.split(p, q, p_q);
    return binaryAverage.split(pq, r, pq_r);
  }
}
