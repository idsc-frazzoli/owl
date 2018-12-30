// code by jph
// adapted from https://github.com/mcapino/trajectorytools
// adapted from https://github.com/AtsushiSakai/PythonRobotics
package ch.ethz.idsc.sophus.dubins;

import java.util.Optional;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/* package */ enum Steer2TurnsDiffSide implements DubinsSteer {
  INSTANCE;
  // ---
  @Override // from DubinsSteer
  public Optional<Tensor> steer(double dist_tr, double th_tr, double th_total, Scalar _radius) {
    double radius = _radius.number().doubleValue();
    double aux = 2.0 * radius / dist_tr;
    if (1 < aux) // if intersecting, no tangent line
      return Optional.empty();
    double th_aux = Math.asin(aux);
    return Optional.of(Tensors.vector( //
        radius * StaticHelper.principalValue(th_tr + th_aux), //
        dist_tr * Math.cos(th_aux), //
        radius * StaticHelper.principalValue(th_tr + th_aux - th_total)));
  }
}
