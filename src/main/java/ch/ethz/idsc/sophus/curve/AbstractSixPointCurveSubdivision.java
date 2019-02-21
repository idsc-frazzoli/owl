// code by jph
package ch.ethz.idsc.sophus.curve;

import ch.ethz.idsc.sophus.math.GeodesicInterface;
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
    int length = tensor.length();
    Tensor curve = Tensors.empty();
    for (int index = 0; index < length; ++index) {
      int first = Math.floorMod(index - 2, length);
      Tensor p = tensor.get((first + 0) % length);
      Tensor q = tensor.get((first + 1) % length);
      Tensor r = tensor.get(index);
      Tensor s = tensor.get((first + 3) % length);
      Tensor t = tensor.get((first + 4) % length);
      Tensor u = tensor.get((first + 5) % length);
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
