// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.owl.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.ScalarQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

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
    Tensor curve = Tensors.empty();
    for (int index = 0; index < tensor.length(); ++index) {
      Tensor p = tensor.get((index - 1 + tensor.length()) % tensor.length());
      Tensor q = tensor.get(index);
      Tensor r = tensor.get((index + 1) % tensor.length());
      Tensor s = tensor.get((index + 2) % tensor.length());
      curve.append(quinte(p, q, r));
      curve.append(center(p, q, r, s));
    }
    return curve;
  }

  @Override // from CurveSubdivision
  public Tensor string(Tensor tensor) {
    if (tensor.length() < 4)
      return super.string(tensor); // cubic BSpline3
    return refine(tensor);
  }

  private Tensor refine(Tensor tensor) {
    Tensor curve = Tensors.empty();
    {
      Tensor q = tensor.get(0);
      Tensor r = tensor.get(1);
      curve.append(q);
      curve.append(center(q, r));
    }
    for (int index = 1; index < tensor.length() - 2; ++index) {
      Tensor p = tensor.get(index - 1);
      Tensor q = tensor.get(index);
      Tensor r = tensor.get(index + 1);
      Tensor s = tensor.get(index + 2);
      curve.append(quinte(p, q, r));
      curve.append(center(p, q, r, s));
    }
    {
      int last = tensor.length() - 1;
      Tensor q = tensor.get(last);
      Tensor r = tensor.get(last - 1);
      Tensor s = tensor.get(last - 2);
      curve.append(quinte(q, r, s));
      curve.append(center(q, r));
      curve.append(q);
      // curve.append(tensor.get(last));
    }
    return curve;
  }

  // reposition of point q
  private Tensor quinte(Tensor p, Tensor q, Tensor r) {
    return center( //
        geodesicInterface.split(p, q, _5_8), //
        geodesicInterface.split(r, q, _5_8));
  }

  // insertion between points q and r
  private Tensor center(Tensor p, Tensor q, Tensor r, Tensor s) {
    return center( //
        geodesicInterface.split(p, q, _15_16), //
        geodesicInterface.split(s, r, _15_16));
  }
}
