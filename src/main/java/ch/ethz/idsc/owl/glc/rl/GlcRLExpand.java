// code by ynager
package ch.ethz.idsc.owl.glc.rl;

import ch.ethz.idsc.owl.glc.adapter.GlcExpand;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;

public class GlcRLExpand extends GlcExpand {
  private final RLTrajectoryPlanner rlTrajectoryPlanner;

  public GlcRLExpand(TrajectoryPlanner trajectoryPlanner) {
    super(trajectoryPlanner);
    this.rlTrajectoryPlanner = (RLTrajectoryPlanner) trajectoryPlanner;
  }

  @Override
  public boolean isOptimal() {
    // FIXME YN unfinished
    return false;
  }
}
