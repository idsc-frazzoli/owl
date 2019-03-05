// code by jph
package ch.ethz.idsc.sophus.curve;

import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Tensor;

/** dual scheme */
// TODO implementation is not efficient: expressions in lo,hi are computed twice
public class BSpline4LRCurveSubdivision extends Dual3PointCurveSubdivision {
  /** @param geodesicInterface non-null */
  public BSpline4LRCurveSubdivision(GeodesicInterface geodesicInterface) {
    super(geodesicInterface);
  }

  // @return point between q and r but more towards q
  @Override
  protected Tensor lo(Tensor p, Tensor q, Tensor r) {
    Tensor pq = center(p, q);
    Tensor qr = center(q, r);
    Tensor ppq = center(p, pq);
    Tensor pqq = center(pq, q);
    Tensor qqr = center(q, qr);
    Tensor blo = center(ppq, pqq);
    Tensor bhi = center(pqq, qqr);
    return center(blo, bhi);
  }

  // @return point between q and r but more towards r
  @Override
  protected Tensor hi(Tensor p, Tensor q, Tensor r) {
    Tensor pq = center(p, q);
    Tensor qr = center(q, r);
    Tensor pqq = center(pq, q);
    Tensor qqr = center(q, qr);
    Tensor qrr = center(qr, r);
    Tensor blo = center(pqq, qqr);
    Tensor bhi = center(qqr, qrr);
    return center(blo, bhi);
  }

  protected final Tensor center(Tensor p, Tensor q) {
    return geodesicInterface.split(p, q, RationalScalar.HALF);
  }
}
