// code by jph
package ch.ethz.idsc.sophus.curve;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Unprotect;

public abstract class RefiningBSpline3CurveSubdivision extends AbstractBSpline3CurveSubdivision {
  @Override
  protected final Tensor refine(Tensor tensor) {
    int length = tensor.length();
    Tensor curve = Unprotect.empty(2 * length);
    {
      Tensor q = tensor.get(0);
      Tensor r = tensor.get(1);
      curve.append(q).append(center(q, r));
    }
    int last = length - 1;
    Tensor p = tensor.get(0);
    for (int index = 1; index < last; /* nothing */ ) {
      Tensor q = tensor.get(index);
      Tensor r = tensor.get(++index);
      curve.append(center(p, q, r)).append(center(q, r));
      p = q;
    }
    return curve.append(tensor.get(last));
  }
}
