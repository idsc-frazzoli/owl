// code by jph
package ch.ethz.idsc.sophus.curve;

import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.ScalarQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.Unprotect;

/** cubic B-spline
 * 
 * Dyn/Sharon 2014 p.16 show that the scheme has a contractivity factor of mu = 1/2 */
public class BSpline3CurveSubdivision extends BSpline1CurveSubdivision {
  private static final Scalar _1_4 = RationalScalar.of(1, 4);
  private static final Scalar _3_4 = RationalScalar.of(3, 4);

  // ---
  public BSpline3CurveSubdivision(GeodesicInterface geodesicInterface) {
    super(geodesicInterface);
  }

  @Override // from CurveSubdivision
  public Tensor cyclic(Tensor tensor) {
    ScalarQ.thenThrow(tensor);
    int length = tensor.length();
    Tensor curve = Unprotect.empty(2 * length);
    for (int index = 0; index < length; ++index) {
      Tensor p = tensor.get((index - 1 + length) % length);
      Tensor q = tensor.get(index);
      Tensor r = tensor.get((index + 1) % length);
      curve.append(center(p, q, r)).append(center(q, r));
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
    int length = tensor.length();
    Tensor curve = Unprotect.empty(2 * length);
    {
      Tensor q = tensor.get(0);
      Tensor r = tensor.get(1);
      curve.append(q).append(center(q, r));
    }
    int last = length - 1;
    for (int index = 1; index < last; /* nothing */ ) {
      Tensor p = tensor.get(index - 1);
      Tensor q = tensor.get(index);
      Tensor r = tensor.get(++index);
      curve.append(center(p, q, r)).append(center(q, r));
    }
    return curve.append(tensor.get(last));
  }

  /** @param p
   * @param q
   * @param r
   * @return reposition of point q */
  protected Tensor center(Tensor p, Tensor q, Tensor r) {
    return center( //
        geodesicInterface.split(p, q, _3_4), //
        geodesicInterface.split(q, r, _1_4));
  }
}
