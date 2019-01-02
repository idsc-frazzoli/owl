// original code by Behzad Torkian
// adapted to geodesic averages by jph
package ch.ethz.idsc.sophus.curve;

import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.opt.AbstractInterpolation;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;

/** Neville's algorithm for polynomial interpolation by Eric Harold Neville
 * 
 * https://en.wikipedia.org/wiki/Neville%27s_algorithm */
public class GeodesicNeville extends AbstractInterpolation implements ScalarTensorFunction {
  private final GeodesicInterface geodesicInterface;
  private final Tensor knots;
  private final Tensor tensor;

  public GeodesicNeville(GeodesicInterface geodesicInterface, Tensor knots, Tensor tensor) {
    this.geodesicInterface = geodesicInterface;
    this.knots = knots;
    this.tensor = tensor;
  }

  @Override // from ScalarTensorFunction
  public Tensor apply(Scalar scalar) {
    int length = knots.length();
    Tensor d = tensor.copy();
    for (int j = 1; j < length; ++j)
      for (int i = length - 1; j <= i; --i)
        d.set(geodesicInterface.split(d.get(i), d.get(i - 1), //
            knots.Get(i).subtract(scalar).divide(knots.Get(i).subtract(knots.Get(i - j)))), i);
    return d.get(length - 1);
  }

  @Override
  public Tensor get(Tensor index) {
    return at(VectorQ.requireLength(index, 1).Get(0));
  }

  @Override
  public Tensor at(Scalar index) {
    return apply(index);
  }
}
