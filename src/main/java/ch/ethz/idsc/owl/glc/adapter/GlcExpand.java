// code by jph
package ch.ethz.idsc.owl.glc.adapter;

import java.util.Optional;
import java.util.function.Supplier;

import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;

/** following the observation by ynager the expansion has to continue
 * until merit of queue node is no less than cost of node in goal */
public class GlcExpand {
  private final TrajectoryPlanner trajectoryPlanner;
  private Supplier<Boolean> isContinued = () -> true;
  private int expandCount = 0;

  public GlcExpand(TrajectoryPlanner trajectoryPlanner) {
    this.trajectoryPlanner = trajectoryPlanner;
  }

  /** @return number of expand operations */
  public int getExpandCount() {
    return expandCount;
  }

  public void setContinued(Supplier<Boolean> isContinued) {
    this.isContinued = isContinued;
  }

  /** @param limit */
  public void findAny(int limit) {
    expand(limit, () -> trajectoryPlanner.getBest().isPresent());
  }

  /** @param limit */
  public void untilOptimal(int limit) {
    expand(limit, () -> GlcNodes.isOptimal(trajectoryPlanner));
  }

  private void expand(int limit, Supplier<Boolean> isFinished) {
    while (0 <= --limit && !isFinished.get() && isContinued.get()) {
      Optional<GlcNode> next = trajectoryPlanner.pollNext();
      if (next.isPresent()) {
        trajectoryPlanner.expand(next.get());
        ++expandCount;
      } else { // queue is empty
        System.out.println("*** Queue is empty -- No Goal was found ***");
        break;
      }
    }
  }
}
