// code by jph
package ch.ethz.idsc.owl.bot.se2.glc;

import ch.ethz.idsc.owl.ani.adapter.StateTrajectoryControl;
import ch.ethz.idsc.owl.bot.se2.Se2Wrap;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.nrm.Vector2NormSquared;
import ch.ethz.idsc.tensor.sca.Clip;

/* package */ abstract class Se2TrajectoryControl extends StateTrajectoryControl implements TrajectoryTargetRender {
  // ---
  protected final Clip clip;

  public Se2TrajectoryControl(Clip clip) {
    this.clip = clip;
  }

  @Override // from StateTrajectoryControl
  protected final Scalar pseudoDistance(Tensor x, Tensor y) {
    return Vector2NormSquared.of(Se2Wrap.INSTANCE.difference(x, y));
  }
}
