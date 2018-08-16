// code by ynager
package ch.ethz.idsc.owl.glc.rl;

import java.util.Optional;
import java.util.function.Supplier;

import ch.ethz.idsc.owl.glc.core.GlcNode;

/** following the observation by ynager the expansion has to continue
 * until merit of queue node is no less than cost of node in goal */
// TODO YN currently lot's of redundancy to GlcExpand -> perhaps extend from GlcExpand?, or create common abstract base class
public class GlcRLExpand {
  private final RLTrajectoryPlanner trajectoryPlanner;
  private Supplier<Boolean> isContinued = () -> true;
  private int expandCount = 0;

  public GlcRLExpand(RLTrajectoryPlanner trajectoryPlanner) {
    this.trajectoryPlanner = trajectoryPlanner;
  }

  /** @return number of expand operations */
  public int getExpandCount() {
    return expandCount;
  }

  public void setContinued(Supplier<Boolean> isContinued) {
    this.isContinued = isContinued;
  }

  public void untilOptimal(int limit) {
    expand(limit, this::isOptimal);
  }

  /** iterates until expansion creates a first node goal region
   * 
   * @param limit */
  public void findAny(int limit) {
    expand(limit, () -> trajectoryPlanner.getBest().isPresent());
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

  /** @return true if no node in queue can achieve a lower cost than best node in goal region */
  public boolean isOptimal() {
    // FIXME YN
    return false;
  }
}
