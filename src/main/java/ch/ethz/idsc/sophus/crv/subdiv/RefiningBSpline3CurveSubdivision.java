// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** examples of extensions are
 * {@link BSpline3CurveSubdivision}, and
 * {@link MSpline3CurveSubdivision} */
public abstract class RefiningBSpline3CurveSubdivision extends AbstractBSpline3CurveSubdivision {
  @Override
  protected final Tensor refine(Tensor tensor) {
    int length = tensor.length();
    Tensor curve = Tensors.reserve(2 * length);
    {
      Tensor q = tensor.get(0);
      Tensor r = tensor.get(1);
      curve.append(q).append(midpoint(q, r));
    }
    int last = length - 1;
    Tensor p = tensor.get(0);
    for (int index = 1; index < last; /* nothing */ ) {
      Tensor q = tensor.get(index);
      Tensor r = tensor.get(++index);
      curve.append(center(p, q, r)).append(midpoint(q, r));
      p = q;
    }
    return curve.append(tensor.get(last));
  }
}
