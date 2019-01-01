// original code by Behzad Torkian
// adapted to geodesic averages by jph
package ch.ethz.idsc.sophus.curve;

import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;

/** Neville's algorithm for polynomial interpolation by Eric Harold Neville
 * 
 * https://en.wikipedia.org/wiki/Neville%27s_algorithm */
public class GeodesicNeville implements ScalarTensorFunction {
  private final GeodesicInterface geodesicInterface;
  private final Tensor knots;
  private final Tensor control;

  public GeodesicNeville(GeodesicInterface geodesicInterface, Tensor knots, Tensor control) {
    this.geodesicInterface = geodesicInterface;
    this.knots = knots;
    this.control = control;
  }

  public GeodesicNeville(GeodesicInterface geodesicInterface, Tensor control) {
    this(geodesicInterface, Range.of(0, control.length()), control);
  }

  @Override // from ScalarTensorFunction
  public Tensor apply(Scalar scalar) {
    int length = knots.length();
    Tensor d = control.copy();
    for (int j = 1; j < length; ++j)
      for (int i = length - 1; j <= i; --i)
        d.set(geodesicInterface.split(d.get(i), d.get(i - 1), //
            knots.Get(i).subtract(scalar).divide(knots.Get(i).subtract(knots.Get(i - j)))), i);
    return d.get(length - 1);
  }
}
