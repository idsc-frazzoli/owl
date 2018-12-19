// code by jph
package ch.ethz.idsc.sophus.curve;

import java.io.Serializable;
import java.util.Objects;

import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.ScalarQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** dual scheme */
public abstract class Dual3PointCurveSubdivision implements CurveSubdivision, Serializable {
  private static final Scalar _1_4 = RationalScalar.of(1, 4);
  // ---
  protected final GeodesicInterface geodesicInterface;

  /** @param geodesicInterface non-null */
  public Dual3PointCurveSubdivision(GeodesicInterface geodesicInterface) {
    this.geodesicInterface = Objects.requireNonNull(geodesicInterface);
  }

  @Override // from CurveSubdivision
  public final Tensor cyclic(Tensor tensor) {
    ScalarQ.thenThrow(tensor);
    Tensor curve = Tensors.empty();
    for (int index = 0; index < tensor.length(); ++index) {
      Tensor p = tensor.get((index - 1 + tensor.length()) % tensor.length());
      Tensor q = tensor.get(index);
      Tensor r = tensor.get((index + 1) % tensor.length());
      curve.append(lo(p, q, r)).append(lo(r, q, p));
    }
    return curve;
  }

  @Override // from CurveSubdivision
  public final Tensor string(Tensor tensor) {
    ScalarQ.thenThrow(tensor);
    switch (tensor.length()) {
    case 0:
    case 1:
      return tensor.copy();
    }
    // ---
    Tensor curve = Tensors.empty();
    {
      Tensor p = tensor.get(0);
      Tensor q = tensor.get(1);
      curve.append(lo(p, q)); // TODO there should be a better formula here for BSpline4
    }
    int last = tensor.length() - 1;
    for (int index = 1; index < last; ++index) {
      Tensor p = tensor.get(index - 1);
      Tensor q = tensor.get(index);
      Tensor r = tensor.get(index + 1);
      curve.append(lo(p, q, r)).append(lo(r, q, p));
    }
    {
      Tensor p = tensor.get(last - 1);
      Tensor q = tensor.get(last);
      curve.append(lo(q, p));
    }
    return curve;
  }

  /** @param p
   * @param q
   * @param r
   * @return point between p and q but more towards q */
  protected abstract Tensor lo(Tensor p, Tensor q, Tensor r);

  // point between p and q but more towards p
  private Tensor lo(Tensor p, Tensor q) {
    return geodesicInterface.split(p, q, _1_4);
  }
}
