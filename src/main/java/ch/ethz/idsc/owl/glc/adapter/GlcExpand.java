// code by jph
package ch.ethz.idsc.owl.glc.adapter;

import java.util.Optional;
import java.util.function.Supplier;

import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GlcTrajectoryPlanner;
import ch.ethz.idsc.tensor.Scalars;

/** following the observation by ynager the expansion may continue
 * until merit of queue node is no less than cost of node in goal */
public class GlcExpand extends Expand<GlcNode> {
  public GlcExpand(GlcTrajectoryPlanner trajectoryPlanner) {
    super(trajectoryPlanner);
  }

  /** Hint: the use of findAny is preferred over untilOptimal
   * 
   * iterates until expansion creates a node with edge through the goal region
   * that is optimal with respect to the merits of all remaining nodes in the queue
   * 
   * @param limit */
  public void untilOptimal(int limit) {
    expand(limit, this::isOptimal);
  }

  private void expand(int limit, Supplier<Boolean> isFinished) {
    while (0 <= --limit && !isFinished.get() && isContinued.get()) {
      Optional<GlcNode> next = expandInterface.pollNext();
      if (next.isPresent()) {
        expandInterface.expand(next.get());
        ++expandCount;
      } else { // queue is empty
        System.out.println("*** Queue is empty -- No Goal was found ***");
        break;
      }
    }
  }

  /** @return true if no node in queue can achieve a lower cost than best node in goal region */
  public final boolean isOptimal() {
    Optional<GlcNode> best = expandInterface.getBest();
    return best.isPresent() //
        && isOptimal(best.get());
  }

  public boolean isOptimal(GlcNode best) {
    return Scalars.lessEquals( //
        best.costFromRoot(), //
        // in the current implementation the best node is guaranteed in queue
        ((GlcTrajectoryPlanner) expandInterface).getQueue().iterator().next().merit());
  }
}
