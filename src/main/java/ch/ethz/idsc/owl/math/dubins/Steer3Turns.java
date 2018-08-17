// code by jph
// adapted from https://github.com/mcapino/trajectorytools
// adapted from https://github.com/AtsushiSakai/PythonRobotics
package ch.ethz.idsc.owl.math.dubins;

import java.util.Optional;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/* package */ enum Steer3Turns implements DubinsSteer {
  INSTANCE;
  // ---
  private static final double PI_HALF = Math.PI / 2;

  @Override // from DubinsSteer
  public Optional<Tensor> steer(double dist_tr, double th_tr, double th_total, Scalar _radius) {
    double radius = _radius.number().doubleValue();
    double aux = dist_tr / 4.0 / radius;
    if (1 < aux)
      return Optional.empty();
    double th_aux = Math.acos(aux);
    return Optional.of(Tensors.vector( //
        StaticHelper.principalValue(th_tr + PI_HALF + th_aux), //
        (Math.PI + 2.0 * th_aux), //
        StaticHelper.principalValue(th_total - th_tr + PI_HALF + th_aux)).multiply(_radius));
  }
}
