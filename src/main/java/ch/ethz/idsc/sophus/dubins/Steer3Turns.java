// code by jph
// adapted from https://github.com/mcapino/trajectorytools
// adapted from https://github.com/AtsushiSakai/PythonRobotics
package ch.ethz.idsc.sophus.dubins;

import java.util.Optional;

import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.ArcCos;

/* package */ enum Steer3Turns implements DubinsSteer {
  INSTANCE;
  // ---
  private static final Scalar PI = DoubleScalar.of(Math.PI);
  private static final Scalar PI_HALF = DoubleScalar.of(Math.PI / 2);
  private static final Scalar FOUR = DoubleScalar.of(4);

  @Override // from DubinsSteer
  public Optional<Tensor> steer(Scalar dist_tr, Scalar th_tr, Scalar th_total, Scalar radius) {
    Scalar aux = dist_tr.divide(FOUR).divide(radius);
    if (StaticHelper.greaterThanOne(aux))
      return Optional.empty();
    Scalar th_aux = ArcCos.FUNCTION.apply(aux);
    Scalar th_pha = PI_HALF.add(th_aux);
    return Optional.of(Tensors.of( //
        StaticHelper.principalValue(th_tr.add(th_pha)), //
        PI.add(th_aux).add(th_aux), //
        StaticHelper.principalValue(th_total.subtract(th_tr).add(th_pha))).multiply(radius));
  }
}
