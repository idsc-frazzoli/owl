// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import ch.ethz.idsc.tensor.ScalarQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Last;

/** examples of extensions are
 * LaneRiesenfeld3CurveSubdivision
 * BSpline5CurveSubdivision */
public abstract class AbstractBSpline3CurveSubdivision extends AbstractBSpline1CurveSubdivision {
  @Override // from AbstractBSpline1CurveSubdivision
  public Tensor cyclic(Tensor tensor) {
    ScalarQ.thenThrow(tensor);
    int length = tensor.length();
    if (length < 2)
      return tensor.copy();
    Tensor curve = Tensors.reserve(2 * length);
    Tensor p = Last.of(tensor);
    for (int index = 0; index < length; ++index) {
      Tensor q = tensor.get(index);
      Tensor r = tensor.get((index + 1) % length);
      curve.append(center(p, q, r)).append(midpoint(q, r));
      p = q;
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

  /** @param tensor with at least 2 control points
   * @return subdivision of control points along string */
  protected abstract Tensor refine(Tensor tensor);

  /** @param p
   * @param q
   * @param r
   * @return replacement for control point q */
  protected abstract Tensor center(Tensor p, Tensor q, Tensor r);
}
