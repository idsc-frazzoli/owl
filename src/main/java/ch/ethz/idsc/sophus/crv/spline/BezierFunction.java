// code by jph
package ch.ethz.idsc.sophus.crv.spline;

import java.util.Objects;

import ch.ethz.idsc.tensor.Integers;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.BinaryAverage;
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
  /** @param binaryAverage
   * @param control non-empty tensor
   * @return function parameterized by the interval [0, 1]
   * @throws Exception if given control tensor is empty or a scalar */
  public static ScalarTensorFunction of(BinaryAverage binaryAverage, Tensor control) {
    Integers.requirePositive(control.length());
    return new BezierFunction(Objects.requireNonNull(binaryAverage), control);
  }

  // ---
  private final BinaryAverage binaryAverage;
  private final Tensor control;

  private BezierFunction(BinaryAverage binaryAverage, Tensor control) {
    this.binaryAverage = binaryAverage;
    this.control = control;
  }

  @Override // from ScalarTensorFunction
  public Tensor apply(Scalar scalar) {
    Tensor[] points = control.stream().toArray(Tensor[]::new);
    for (int i = points.length; 1 <= i; --i) {
      int count = -1;
      Tensor p = points[0];
      for (int index = 1; index < i; ++index)
        points[++count] = binaryAverage.split(p, p = points[index], scalar);
    }
    return points[0];
  }
}
