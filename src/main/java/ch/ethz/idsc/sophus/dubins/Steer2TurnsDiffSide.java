// code by jph
// adapted from https://github.com/mcapino/trajectorytools
// adapted from https://github.com/AtsushiSakai/PythonRobotics
package ch.ethz.idsc.sophus.dubins;

import java.util.Optional;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.ArcSin;
import ch.ethz.idsc.tensor.sca.Cos;

/* package */ enum Steer2TurnsDiffSide implements DubinsSteer {
  INSTANCE;
  // ---
  @Override // from DubinsSteer
  public Optional<Tensor> steer(Scalar dist_tr, Scalar th_tr, Scalar th_total, Scalar radius) {
    Scalar aux = radius.add(radius).divide(dist_tr);
    if (StaticHelper.greaterThanOne(aux)) // if intersecting, no tangent line
      return Optional.empty();
    Scalar th_aux = ArcSin.FUNCTION.apply(aux);
    return Optional.of(Tensors.of( //
        radius.multiply(StaticHelper.principalValue(th_tr.add(th_aux))), //
        dist_tr.multiply(Cos.FUNCTION.apply(th_aux)), //
        radius.multiply(StaticHelper.principalValue(th_tr.add(th_aux).subtract(th_total)))));
  }
}
