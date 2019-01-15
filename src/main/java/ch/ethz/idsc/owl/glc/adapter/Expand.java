// code by jph and jl
package ch.ethz.idsc.owl.glc.adapter;

import java.util.Optional;
import java.util.function.Supplier;

import ch.ethz.idsc.owl.data.tree.StateCostNode;
import ch.ethz.idsc.owl.glc.core.ExpandInterface;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.std.StandardTrajectoryPlanner;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.io.Timing;

/** class contains static functions that operate on instances of {@link ExpandInterface}
 * 
 * The expansion of the following planners can be controlled using the functions:
 * {@link StandardTrajectoryPlanner} */
public enum Expand {
  ;
  /** Hint: for planning with GLC use {@link GlcExpand}
   * 
   * expand aborts if isContinued supplies false
   * 
   * @param expandInterface
   * @param expandLimit
   * @param isContinued
   * @return number of function calls of {@link ExpandInterface#expand(GlcNode)} */
  public static <T extends StateCostNode> int maxSteps(ExpandInterface<T> expandInterface, int expandLimit, Supplier<Boolean> isContinued) {
    int expandCount = 0;
    while (0 <= --expandLimit //
        && !expandInterface.getBest().isPresent() //
        && isContinued.get()) {
      Optional<T> next = expandInterface.pollNext();
      if (next.isPresent()) { // queue is empty
        expandInterface.expand(next.get());
        ++expandCount;
      } else {
        System.out.println("*** Queue is empty -- No Goal was found ***");
        break;
      }
    }
    return expandCount;
  }

  /** expands until the time of the running algorithm exceeds the maxTime or a goal was found
   * 
   * @param expandInterface
   * @param timeLimit of expand function in [s]
   * @return number of function calls of {@link ExpandInterface#expand(GlcNode)} */
  public static <T extends StateCostNode> int maxTime(ExpandInterface<T> expandInterface, Scalar timeLimit) {
    System.out.println("*** EXPANDING ***");
    Timing timing = Timing.started();
    final double time = timeLimit.number().doubleValue();
    int expandCount = 0;
    while (true) {
      Optional<T> next = expandInterface.pollNext();
      if (!next.isPresent()) {
        System.err.println("**** Queue is empty -- No Goal was found"); // queue is empty
        break;
      }
      expandInterface.expand(next.get());
      ++expandCount;
      if (expandInterface.getBest().isPresent()) { // found node in goal region
        timing.stop();
        System.out.println("after " + timing.seconds() + "s");
        break;
      }
      if (time < timing.seconds()) {
        System.out.println("*** TimeLimit reached -- No Goal was found ***");
        break;
      }
    }
    return expandCount;
  }

  /** fixed number of invocations of expand(...)
   * however, earlier abort may be possible for instance to due lack of nodes to expand from
   * 
   * function used by GLC as well as RRTS planner
   * 
   * @param expandInterface
   * @param expandLimit
   * @return number of function calls of {@link ExpandInterface#expand(GlcNode)} */
  public static <T extends StateCostNode> int steps(ExpandInterface<T> expandInterface, int expandLimit) {
    int expandCount = 0;
    while (expandCount < expandLimit) {
      Optional<T> next = expandInterface.pollNext();
      if (!next.isPresent()) { // queue is empty
        System.out.println("*** Queue is empty -- No Goal was found ***");
        break;
      }
      expandInterface.expand(next.get());
      ++expandCount;
    }
    return expandCount;
  }
}
