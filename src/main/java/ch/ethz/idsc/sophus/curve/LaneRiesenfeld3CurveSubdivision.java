// code by jph
package ch.ethz.idsc.sophus.curve;

import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.ScalarQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Last;

/** cubic B-spline
 * 
 * Dyn/Sharon 2014 p.16 show that the scheme has a contractivity factor of mu = 1/2 */
public class LaneRiesenfeld3CurveSubdivision extends BSpline1CurveSubdivision {
  public LaneRiesenfeld3CurveSubdivision(GeodesicInterface geodesicInterface) {
    super(geodesicInterface);
  }

  @Override // from CurveSubdivision
  public Tensor cyclic(Tensor tensor) {
    ScalarQ.thenThrow(tensor);
    if (tensor.length() < 2)
      return tensor.copy();
    Tensor curve = Tensors.empty();
    int length = tensor.length();
    Tensor pq = center(Last.of(tensor), tensor.get(0));
    for (int index = 0; index < length; /* nothing */ ) {
      Tensor q = tensor.get(index);
      Tensor r = tensor.get(++index % length);
      Tensor qr = center(q, r);
      curve.append(center(pq, qr, q)).append(qr);
      pq = qr;
    }
    return curve;
  }

  @Override // from CurveSubdivision
  public Tensor string(Tensor tensor) {
    switch (tensor.length()) {
    case 0:
      return Tensors.empty();
    case 1:
      return tensor.copy();
    default:
      return refine(tensor);
    }
  }

  private Tensor refine(Tensor tensor) {
    Tensor curve = Tensors.empty();
    Tensor pq;
    {
      Tensor q = tensor.get(0);
      Tensor r = tensor.get(1);
      pq = center(q, r); // notation is deliberate
      curve.append(q).append(pq);
    }
    int last = tensor.length() - 1;
    for (int index = 1; index < last; /* nothing */ ) {
      Tensor q = tensor.get(index);
      Tensor r = tensor.get(++index);
      Tensor qr = center(q, r);
      curve.append(center(pq, qr, q)).append(qr);
      pq = qr;
    }
    return curve.append(tensor.get(last));
  }

  private Tensor center(Tensor pq, Tensor qr, Tensor q) {
    return center(center(pq, q), center(q, qr));
  }
}
