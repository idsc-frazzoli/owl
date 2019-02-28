// code by jph
package ch.ethz.idsc.sophus.curve;

import java.io.Serializable;

import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.ScalarQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Last;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;

/** quadratic B-spline
 * Chaikin 1965 */
public class BSpline2CurveSubdivision implements CurveSubdivision, Serializable {
  private static final Scalar _1_4 = RationalScalar.of(1, 4);
  private static final Scalar _3_4 = RationalScalar.of(3, 4);
  // ---
  private final GeodesicInterface geodesicInterface;

  public BSpline2CurveSubdivision(GeodesicInterface geodesicInterface) {
    this.geodesicInterface = geodesicInterface;
  }

  @Override // from CurveSubdivision
  public Tensor cyclic(Tensor tensor) {
    return refine(string(tensor), Last.of(tensor), tensor.get(0));
  }

  // Hint: curve contracts at the sides
  @Override // from CurveSubdivision
  public Tensor string(Tensor tensor) {
    ScalarQ.thenThrow(tensor);
    int length = tensor.length();
    if (length < 2)
      return tensor.copy();
    Tensor curve = Tensors.empty();
    Tensor p = tensor.get(0);
    for (int index = 1; index < length; ++index) {
      Tensor q = tensor.get(index);
      refine(curve, p, q);
      p = q;
    }
    return curve;
  }

  private Tensor refine(Tensor curve, Tensor p, Tensor q) {
    ScalarTensorFunction scalarTensorFunction = geodesicInterface.curve(p, q);
    return curve.append(scalarTensorFunction.apply(_1_4)).append(scalarTensorFunction.apply(_3_4));
  }
}
