// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.owl.math.GeodesicInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** dual scheme */
public class Split2LoDual3PointCurveSubdivision extends Dual3PointCurveSubdivision {
  private final GeodesicInterface geodesicInterface;
  private final Scalar p_q;
  private final Scalar pq_r;

  public Split2LoDual3PointCurveSubdivision(GeodesicInterface geodesicInterface, Scalar p_q, Scalar pq_r) {
    super(geodesicInterface);
    this.geodesicInterface = geodesicInterface;
    this.p_q = p_q;
    this.pq_r = pq_r;
  }

  @Override // from Dual3PointCurveSubdivision
  protected Tensor lo(Tensor p, Tensor q, Tensor r) {
    Tensor pq = geodesicInterface.split(p, q, p_q);
    return geodesicInterface.split(pq, r, pq_r);
  }
}
