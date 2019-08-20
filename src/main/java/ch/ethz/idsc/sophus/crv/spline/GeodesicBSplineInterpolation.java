// code by jph
package ch.ethz.idsc.sophus.crv.spline;

import ch.ethz.idsc.sophus.math.SplitInterface;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public class GeodesicBSplineInterpolation extends AbstractBSplineInterpolation {
  private static final Scalar TWO = RealScalar.of(2);
  // ---
  private final SplitInterface splitInterface;

  public GeodesicBSplineInterpolation(SplitInterface splitInterface, int degree, Tensor target) {
    super(splitInterface, degree, target);
    this.splitInterface = splitInterface;
  }

  @Override // from AbstractBSplineInterpolation
  protected Tensor move(Tensor p, Tensor e, Tensor t) {
    Tensor pt = splitInterface.split(p, t, RationalScalar.HALF);
    Tensor et = splitInterface.split(e, t, RationalScalar.HALF);
    Tensor tf = splitInterface.split(et, pt, TWO); // transfer
    return splitInterface.split(p, tf, TWO); // push
  }
}
