// code by astoll
package ch.ethz.idsc.owl.glc.rl2;

import ch.ethz.idsc.owl.glc.adapter.GlcExpand;
import ch.ethz.idsc.owl.glc.core.GlcNode;

public class RelaxedGlcExpand extends GlcExpand {
  private final RelaxedTrajectoryPlanner relaxedTrajectoryPlanner;

  public RelaxedGlcExpand(RelaxedTrajectoryPlanner relaxedTrajectoryPlanner) {
    super(relaxedTrajectoryPlanner);
    this.relaxedTrajectoryPlanner = relaxedTrajectoryPlanner;
  }

  @Override // from GlcExpand
  public boolean isOptimal(GlcNode best) {
    // in the current implementation the best node is guaranteed to be in the global queue
    return best.equals(relaxedTrajectoryPlanner.peekBestFromGlobal());
  }
}
