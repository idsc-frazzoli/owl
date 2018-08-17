// code by ynager
package ch.ethz.idsc.owl.glc.rl;

import ch.ethz.idsc.owl.data.GlobalAssert;
import ch.ethz.idsc.owl.glc.adapter.GlcExpand;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;

public class GlcRLExpand extends GlcExpand {
  public GlcRLExpand(TrajectoryPlanner trajectoryPlanner) {
    super(trajectoryPlanner);
    GlobalAssert.that(trajectoryPlanner instanceof RLTrajectoryPlanner);
  }

  /** @return true if no node in queue can achieve a lower cost than best node in goal region */
  @Override
  public boolean isOptimal() {
    return false; // FIXME YN
  }
}
