// code by jph
package ch.ethz.idsc.sophus.crv.spline;

import ch.ethz.idsc.sophus.math.SplitInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;

/** <a href="https://en.wikipedia.org/wiki/De_Casteljau%27s_algorithm">
 * De Casteljau's algorithm</a> for the evaluation of Bezier curves.
 *
 * <p>For parameters in the unit interval [0, 1] the function gives
 * values "in between" the control points.
 * 
 * <p>BezierFunction can be used for extrapolation when using
 * parameters outside the interval.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/BezierFunction.html">BezierFunction</a> */
public class BezierFunction implements ScalarTensorFunction {
  /** @param splitInterface
   * @param control non-empty tensor
   * @return function parameterized by the interval [0, 1]
   * @throws Exception if given control tensor is empty or a scalar */
  public static ScalarTensorFunction of(SplitInterface splitInterface, Tensor control) {
    if (control.length() < 1)
      throw TensorRuntimeException.of(control);
    return new BezierFunction(splitInterface, control);
  }

  // ---
  private final SplitInterface splitInterface;
  private final Tensor control;

  private BezierFunction(SplitInterface splitInterface, Tensor control) {
    this.splitInterface = splitInterface;
    this.control = control;
  }

  @Override // from ScalarTensorFunction
  public Tensor apply(Scalar scalar) {
    Tensor points = this.control;
    while (1 < points.length()) {
      Tensor tensor = Tensors.empty();
      Tensor p = points.get(0);
      for (int index = 1; index < points.length(); ++index) {
        Tensor q = points.get(index);
        tensor.append(splitInterface.split(p, q, scalar));
        p = q;
      }
      points = tensor;
    }
    return points.get(0);
  }
}
