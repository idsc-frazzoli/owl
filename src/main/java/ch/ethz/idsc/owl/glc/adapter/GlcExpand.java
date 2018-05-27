// code by jph
package ch.ethz.idsc.owl.glc.adapter;

import java.util.Optional;
import java.util.function.Supplier;

import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;

/** following the observation by ynager the expansion has to continue
 * until merit of queue node is no less than cost of node in goal */
public enum GlcExpand {
  ;
  public static int maxSteps(TrajectoryPlanner trajectoryPlanner, int expandLimit, Supplier<Boolean> isContinued) {
    int expandCount = 0;
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
      Optional<GlcNode> best = trajectoryPlanner.getBest();
      if (best.isPresent()) { // found node in goal region
        // if (Objects.isNull(costHi)) {
        // costHi = best.get().costFromRoot();
        // System.out.println("costHi =" + costHi);
        // }
        // in the current implementation the best node is always in queue
        Scalar merit = trajectoryPlanner.getQueue().iterator().next().merit();
        Scalar costLo = best.get().costFromRoot();
        if (Scalars.lessEquals(costLo, merit)) {
          // System.out.println("costLo =" + costLo);
          // System.out.println("phase2Count=" + phase2Count + "/" + expandCount);
          break;
        }
        // if (phase2Count == 0)
        // System.out.println("merit = " + merit);
        // ++phase2Count;
      }
      if (!isContinued.get())
        break;
    }
    return expandCount;
  }
}
