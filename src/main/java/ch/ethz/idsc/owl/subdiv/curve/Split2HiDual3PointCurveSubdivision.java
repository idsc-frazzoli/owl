// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** dual scheme */
public class Split2HiDual3PointCurveSubdivision extends Dual3PointCurveSubdivision {
  private final GeodesicInterface geodesicInterface;
  private final Scalar q_r;
  private final Scalar p_qr;

  public Split2HiDual3PointCurveSubdivision(GeodesicInterface geodesicInterface, Scalar p_qr, Scalar q_r) {
    super(geodesicInterface);
    this.geodesicInterface = geodesicInterface;
    this.q_r = q_r;
    this.p_qr = p_qr;
  }

  @Override // from Dual3PointCurveSubdivision
  protected Tensor lo(Tensor p, Tensor q, Tensor r) {
    Tensor qr = geodesicInterface.split(q, r, q_r);
    return geodesicInterface.split(p, qr, p_qr);
  }
}
