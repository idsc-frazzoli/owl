// original code by Behzad Torkian
// adapted to geodesic averages by jph
package ch.ethz.idsc.sophus.crv.spline;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.opt.AbstractInterpolation;
import ch.ethz.idsc.tensor.opt.BinaryAverage;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;

/** Neville's algorithm for polynomial interpolation by Eric Harold Neville
 * 
 * https://en.wikipedia.org/wiki/Neville%27s_algorithm */
public class GeodesicNeville extends AbstractInterpolation implements ScalarTensorFunction {
  private final BinaryAverage binaryAverage;
  private final Tensor knots;
  private final Tensor tensor;

  public GeodesicNeville(BinaryAverage binaryAverage, Tensor knots, Tensor tensor) {
    this.binaryAverage = binaryAverage;
    this.knots = knots;
    this.tensor = tensor;
  }

  @Override // from ScalarTensorFunction
  public Tensor apply(Scalar scalar) {
    int length = knots.length();
    Tensor d = tensor.copy();
    for (int j = 1; j < length; ++j)
      for (int i = length - 1; j <= i; --i)
        d.set(binaryAverage.split(d.get(i), d.get(i - 1), //
            knots.Get(i).subtract(scalar).divide(knots.Get(i).subtract(knots.Get(i - j)))), i);
    return d.get(length - 1);
  }

  @Override // from Interpolation
  public Tensor get(Tensor index) {
    return at(VectorQ.requireLength(index, 1).Get(0));
  }

  @Override // from Interpolation
  public Tensor at(Scalar index) {
    return apply(index);
  }
}
