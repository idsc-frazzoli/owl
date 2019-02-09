// code by jph
package ch.ethz.idsc.sophus.filter;

import ch.ethz.idsc.sophus.curve.BezierFunction;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/** extrapolation by evaluating the Bezier curve defined by n number of
 * control points at parameter value n / (n - 1) */
public class BezierExtrapolation implements TensorUnaryOperator {
  /** @param geodesicInterface
   * @return */
  public static TensorUnaryOperator of(GeodesicInterface geodesicInterface) {
    return new BezierExtrapolation(geodesicInterface);
  }

  // ---
  private final GeodesicInterface geodesicInterface;

  private BezierExtrapolation(GeodesicInterface geodesicInterface) {
    this.geodesicInterface = geodesicInterface;
  }

  @Override // from TensorUnaryOperator
  public Tensor apply(Tensor tensor) {
    ScalarTensorFunction scalarTensorFunction = BezierFunction.of(geodesicInterface, tensor);
    int n = tensor.length();
    return scalarTensorFunction.apply(RationalScalar.of(n, n - 1));
  }
}
