// code by jph
// adapted from https://github.com/mcapino/trajectorytools
// adapted from https://github.com/AtsushiSakai/PythonRobotics
package ch.ethz.idsc.sophus.crv.dubins;

import java.util.Optional;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/* package */ enum Steer2TurnsSameSide implements DubinsSteer {
  INSTANCE;
  // ---
  @Override // from DubinsSteer
  public Optional<Tensor> steer(Scalar dist_tr, Scalar th_tr, Scalar th_total, Scalar radius) {
    return Optional.of(Tensors.of( //
        radius.multiply(th_tr), //
        dist_tr, //
        radius.multiply(StaticHelper.principalValue(th_total.subtract(th_tr)))));
  }
}
