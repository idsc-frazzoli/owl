// code by jph
package ch.ethz.idsc.sophus.crv.spline;

import ch.ethz.idsc.sophus.math.SplitInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public class GeodesicBSplineInterpolation extends AbstractBSplineInterpolation {
  private static final Scalar TWO = RealScalar.of(2);

  public GeodesicBSplineInterpolation(SplitInterface binaryAverage, int degree, Tensor target) {
    super(binaryAverage, degree, target);
  }

  @Override // from AbstractBSplineInterpolation
  protected Tensor move(Tensor p, Tensor e, Tensor t) {
    Tensor pt = splitInterface.midpoint(p, t);
    Tensor et = splitInterface.midpoint(e, t);
    Tensor tf = splitInterface.split(et, pt, TWO); // transfer
    return splitInterface.split(p, tf, TWO); // push
  }
}
