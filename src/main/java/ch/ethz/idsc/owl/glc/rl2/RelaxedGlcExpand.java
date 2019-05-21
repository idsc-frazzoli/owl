// code by astoll
package ch.ethz.idsc.owl.glc.rl2;

import java.util.Optional;

import ch.ethz.idsc.owl.glc.adapter.GlcExpand;
import ch.ethz.idsc.owl.glc.core.GlcNode;

public class RelaxedGlcExpand extends GlcExpand {
  private final RelaxedTrajectoryPlanner trajectoryPlanner;

  public RelaxedGlcExpand(RelaxedTrajectoryPlanner trajectoryPlanner) {
    super(trajectoryPlanner);
    this.trajectoryPlanner = trajectoryPlanner;
  }

  /** @return true if best node in goal region is best node in global queue */
  @Override
  public boolean isOptimal() {
    Optional<GlcNode> best = trajectoryPlanner.getBest();
    // in the current implementation the best node is guaranteed to be in the global queue
    return best.isPresent() && best.get().equals(trajectoryPlanner.peekBestFromGlobal());
  }
}
