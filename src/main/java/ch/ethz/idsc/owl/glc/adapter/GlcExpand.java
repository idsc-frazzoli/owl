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

  public void setContinued(Supplier<Boolean> isContinued) {
    this.isContinued = isContinued;
  }

  public void firstGoal(int expandLimit) {
    // int phase2Count = 0;
    // Scalar costHi = null;
    while (expandCount < expandLimit) {
      Optional<GlcNode> next = trajectoryPlanner.pollNext();
      if (!next.isPresent()) { // queue is empty
        System.out.println("*** Queue is empty -- No Goal was found ***");
        break;
      }
      trajectoryPlanner.expand(next.get());
      ++expandCount;
      // ---
      if (trajectoryPlanner.getBest().isPresent()) // found node in goal region
        break;
      if (!isContinued.get())
        break;
    }
  }

  public void maxSteps(int expandLimit) {
    // int phase2Count = 0;
    // Scalar costHi = null;
    while (expandCount < expandLimit) {
      Optional<GlcNode> next = trajectoryPlanner.pollNext();
      if (!next.isPresent()) { // queue is empty
        System.out.println("*** Queue is empty -- No Goal was found ***");
        break;
      }
      trajectoryPlanner.expand(next.get());
      ++expandCount;
      // ---
      if (GlcNodes.isOptimal(trajectoryPlanner))
        break;
      if (!isContinued.get())
        break;
    }
  }

  public int getExpandCount() {
    return expandCount;
  }
}
