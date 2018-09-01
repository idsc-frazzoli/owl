// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.tensor.ScalarQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public abstract class AbstractSixPointCurveSubdivision extends FourPointCurveSubdivision {
  public AbstractSixPointCurveSubdivision(GeodesicInterface geodesicInterface) {
    super(geodesicInterface);
  }

  @Override // from FourPointCurveSubdivision
  public final Tensor cyclic(Tensor tensor) {
    ScalarQ.thenThrow(tensor);
    Tensor curve = Tensors.empty();
    for (int index = 0; index < tensor.length(); ++index) {
      Tensor p = tensor.get((index - 2 + tensor.length()) % tensor.length());
      Tensor q = tensor.get((index - 1 + tensor.length()) % tensor.length());
      Tensor r = tensor.get(index);
      Tensor s = tensor.get((index + 1) % tensor.length());
      Tensor t = tensor.get((index + 2) % tensor.length());
      Tensor u = tensor.get((index + 3) % tensor.length());
      curve.append(r).append(center(p, q, r, s, t, u));
    }
    return curve;
  }

  @Override // from FourPointCurveSubdivision
  public final Tensor string(Tensor tensor) {
    if (tensor.length() < 6)
      return super.string(tensor);
    // ---
    Tensor curve = Tensors.empty();
    {
      Tensor p = tensor.get(0);
      Tensor q = tensor.get(1);
      Tensor r = tensor.get(2);
      curve.append(p).append(triple(p, q, r));
      Tensor s = tensor.get(3);
      curve.append(q).append(center(p, q, r, s));
    }
    int last = tensor.length() - 3;
    for (int index = 2; index < last; ++index) {
      Tensor p = tensor.get(index - 2);
      Tensor q = tensor.get(index - 1);
      Tensor r = tensor.get(index);
      Tensor s = tensor.get(index + 1);
      Tensor t = tensor.get(index + 2);
      Tensor u = tensor.get(index + 3);
      curve.append(r).append(center(p, q, r, s, t, u));
    }
    {
      Tensor p = tensor.get(last + 2);
      Tensor q = tensor.get(last + 1);
      Tensor r = tensor.get(last);
      Tensor s = tensor.get(last - 1);
      curve.append(r).append(center(p, q, r, s));
      curve.append(q).append(triple(p, q, r)).append(p);
    }
    return curve;
  }

  protected abstract Tensor center(Tensor p, Tensor q, Tensor r, Tensor s, Tensor t, Tensor u);
}
