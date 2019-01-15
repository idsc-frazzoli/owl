// code by jph
package ch.ethz.idsc.sophus.curve;

import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public class GeodesicBSplineInterpolation extends AbstractBSplineInterpolation {
  private static final Scalar TWO = RealScalar.of(2);
  // ---
  private final GeodesicInterface geodesicInterface;

  public GeodesicBSplineInterpolation(GeodesicInterface geodesicInterface, int degree, Tensor target) {
    super(geodesicInterface, degree, target);
    this.geodesicInterface = geodesicInterface;
  }

  @Override
  protected Tensor move(Tensor p, Tensor e, Tensor t) {
    Tensor pt = geodesicInterface.split(p, t, RationalScalar.HALF);
    Tensor et = geodesicInterface.split(e, t, RationalScalar.HALF);
    Tensor tf = geodesicInterface.split(et, pt, TWO); // transfer
    return geodesicInterface.split(p, tf, TWO); // push
  }
}
