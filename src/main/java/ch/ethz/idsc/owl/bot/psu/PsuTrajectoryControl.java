// code by jph
package ch.ethz.idsc.owl.bot.psu;

import ch.ethz.idsc.owl.ani.adapter.StateTrajectoryControl;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/* package */ class PsuTrajectoryControl extends StateTrajectoryControl {
  @Override // from StateTrajectoryControl
  protected Scalar pseudoDistance(Tensor x, Tensor y) {
    return PsuMetric.INSTANCE.distance(x, y);
  }
}
