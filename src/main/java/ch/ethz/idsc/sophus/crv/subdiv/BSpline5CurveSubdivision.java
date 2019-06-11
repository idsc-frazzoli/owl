// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.ScalarQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.alg.Last;

/** quintic B-spline is implemented as an extension of
 * cubic B-spline refinement */
public class BSpline5CurveSubdivision extends BSpline3CurveSubdivision {
  private static final Scalar _5_8 = RationalScalar.of(5, 8);
  private static final Scalar _15_16 = RationalScalar.of(15, 16);

  // ---
  public BSpline5CurveSubdivision(GeodesicInterface geodesicInterface) {
    super(geodesicInterface);
  }

  @Override // from CurveSubdivision
  public Tensor cyclic(Tensor tensor) {
    ScalarQ.thenThrow(tensor);
    int length = tensor.length();
    if (length < 2)
      return tensor.copy();
    Tensor curve = Unprotect.empty(2 * length);
    Tensor p = Last.of(tensor);
    for (int index = 0; index < length; ++index) {
      Tensor q = tensor.get(index);
      Tensor r = tensor.get((index + 1) % length);
      Tensor s = tensor.get((index + 2) % length);
      curve.append(quinte(p, q, r));
      curve.append(center(p, q, r, s));
      p = q;
    }
    return curve;
  }

  @Override // from CurveSubdivision
  public Tensor string(Tensor tensor) {
    if (tensor.length() < 4)
      return super.string(tensor); // cubic BSpline3
    return private_refine(tensor);
  }

  private Tensor private_refine(Tensor tensor) {
    int length = tensor.length();
    Tensor curve = Unprotect.empty(2 * length);
    {
      Tensor q = tensor.get(0);
      Tensor r = tensor.get(1);
      curve.append(q);
      curve.append(midpoint(q, r));
    }
    Tensor p = tensor.get(0);
    for (int index = 1; index < length - 2; ++index) {
      Tensor q = tensor.get(index);
      Tensor r = tensor.get(index + 1);
      Tensor s = tensor.get(index + 2);
      curve.append(quinte(p, q, r));
      curve.append(center(p, q, r, s));
      p = q;
    }
    {
      int last = length - 1;
      Tensor q = tensor.get(last);
      Tensor r = tensor.get(last - 1);
      Tensor s = tensor.get(last - 2);
      curve.append(quinte(q, r, s));
      curve.append(midpoint(q, r));
      curve.append(q);
    }
    return curve;
  }

  // reposition of point q
  private Tensor quinte(Tensor p, Tensor q, Tensor r) {
    return midpoint( //
        geodesicInterface.split(p, q, _5_8), //
        geodesicInterface.split(r, q, _5_8));
  }

  // insertion between points q and r
  private Tensor center(Tensor p, Tensor q, Tensor r, Tensor s) {
    return midpoint( //
        geodesicInterface.split(p, q, _15_16), //
        geodesicInterface.split(s, r, _15_16));
  }
}
