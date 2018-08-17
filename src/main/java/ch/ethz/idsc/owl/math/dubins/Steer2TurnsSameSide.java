// code by jph
// adapted from https://github.com/mcapino/trajectorytools
// adapted from https://github.com/AtsushiSakai/PythonRobotics
package ch.ethz.idsc.owl.math.dubins;

import java.util.Optional;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/* package */ enum Steer2TurnsSameSide implements DubinsSteer {
  INSTANCE;
  // ---
  @Override // from DubinsSteer
  public Optional<Tensor> steer(double dist_tr, double th_tr, double th_total, Scalar _radius) {
    double radius = _radius.number().doubleValue();
    return Optional.of(Tensors.vector( //
        radius * th_tr, //
        dist_tr, //
        radius * StaticHelper.principalValue(th_total - th_tr)));
  }
}
