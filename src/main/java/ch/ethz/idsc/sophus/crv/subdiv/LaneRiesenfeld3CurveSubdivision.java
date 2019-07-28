// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import java.util.Objects;

import ch.ethz.idsc.sophus.math.MidpointInterface;
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
 * Reference:
 * "A theoretical development for the computer generation of piecewise polynomial surfaces"
 * by J. M. Lane and R. F. Riesenfeld; IEEE Trans. Pattern Anal. Machine Intell. 2 (1980), 35-46 */
public final class LaneRiesenfeld3CurveSubdivision extends AbstractBSpline3CurveSubdivision {
  public static CurveSubdivision of(MidpointInterface midpointInterface) {
    return new LaneRiesenfeld3CurveSubdivision(Objects.requireNonNull(midpointInterface));
  }

  // ---
  private final MidpointInterface midpointInterface;

  private LaneRiesenfeld3CurveSubdivision(MidpointInterface midpointInterface) {
    this.midpointInterface = midpointInterface;
  }

  @Override // from CurveSubdivision
  public Tensor cyclic(Tensor tensor) {
    ScalarQ.thenThrow(tensor);
    int length = tensor.length();
    if (length < 2)
      return tensor.copy();
    Tensor curve = Unprotect.empty(2 * length);
    Tensor pq = midpoint(Last.of(tensor), tensor.get(0));
    for (int index = 0; index < length; /* nothing */ ) {
      Tensor q = tensor.get(index);
      Tensor r = tensor.get(++index % length);
      Tensor qr = midpoint(q, r);
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
      pq = midpoint(q, r); // notation is deliberate
      curve.append(q).append(pq);
    }
    int last = length - 1;
    for (int index = 1; index < last; /* nothing */ ) {
      Tensor q = tensor.get(index);
      Tensor r = tensor.get(++index);
      Tensor qr = midpoint(q, r);
      curve.append(center(pq, q, qr)).append(qr);
      pq = qr;
    }
    return curve.append(tensor.get(last));
  }

  @Override // from AbstractBSpline3CurveSubdivision
  protected Tensor center(Tensor pq, Tensor q, Tensor qr) {
    return midpoint(midpoint(pq, q), midpoint(q, qr));
  }

  @Override // from AbstractBSpline1CurveSubdivision
  public Tensor midpoint(Tensor p, Tensor q) {
    return midpointInterface.midpoint(p, q);
  }
}
