// code by jph
package ch.ethz.idsc.sophus.curve;

import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.ScalarQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.alg.Last;

/** subdivision scheme with linear subdivision for mid-point insertion and
 * LaneRiesenfeldCurveSubdivision with degree 3 for vertex reposition.
 * 
 * the computational complexity of LaneRiesenfeld3CurveSubdivision is
 * between cubic bspline and LaneRiesenfeldCurveSubdivision with degree 3.
 * 
 * LaneRiesenfeldCurveSubdivision with degree 3 produces better curvature for
 * Clothoid geodesics than LaneRiesenfeld3CurveSubdivision. */
public final class LaneRiesenfeld3CurveSubdivision extends AbstractBSpline3CurveSubdivision {
  private final GeodesicInterface geodesicInterface;

  public LaneRiesenfeld3CurveSubdivision(GeodesicInterface geodesicInterface) {
    this.geodesicInterface = geodesicInterface;
  }

  @Override // from CurveSubdivision
  public Tensor cyclic(Tensor tensor) {
    ScalarQ.thenThrow(tensor);
    if (tensor.length() < 2)
      return tensor.copy();
    int length = tensor.length();
    Tensor curve = Unprotect.empty(2 * length);
    Tensor pq = center(Last.of(tensor), tensor.get(0));
    for (int index = 0; index < length; /* nothing */ ) {
      Tensor q = tensor.get(index);
      Tensor r = tensor.get(++index % length);
      Tensor qr = center(q, r);
      curve.append(center(pq, q, qr)).append(qr);
      pq = qr;
    }
    return curve;
  }

  @Override // from AbstractBSpline3CurveSubdivision
  protected Tensor refine(Tensor tensor) {
    int length = tensor.length();
    Tensor curve = Unprotect.empty(2 * length);
    Tensor pq;
    {
      Tensor q = tensor.get(0);
      Tensor r = tensor.get(1);
      pq = center(q, r); // notation is deliberate
      curve.append(q).append(pq);
    }
    int last = length - 1;
    for (int index = 1; index < last; /* nothing */ ) {
      Tensor q = tensor.get(index);
      Tensor r = tensor.get(++index);
      Tensor qr = center(q, r);
      curve.append(center(pq, q, qr)).append(qr);
      pq = qr;
    }
    return curve.append(tensor.get(last));
  }

  @Override // from AbstractBSpline3CurveSubdivision
  protected Tensor center(Tensor pq, Tensor q, Tensor qr) {
    return center(center(pq, q), center(q, qr));
  }

  @Override // from AbstractBSpline1CurveSubdivision
  protected Tensor center(Tensor p, Tensor q) {
    return geodesicInterface.split(p, q, RationalScalar.HALF);
  }
}
