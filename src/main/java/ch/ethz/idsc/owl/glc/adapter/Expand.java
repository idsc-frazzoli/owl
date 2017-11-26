// code by jph and jl
package ch.ethz.idsc.owl.glc.adapter;

import java.util.Optional;
import java.util.function.Supplier;

import ch.ethz.idsc.owl.data.Stopwatch;
import ch.ethz.idsc.owl.data.tree.StateCostNode;
import ch.ethz.idsc.owl.glc.core.ExpandInterface;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.std.StandardTrajectoryPlanner;
import ch.ethz.idsc.tensor.Scalar;

/** class contains static functions that operate on instances of {@link ExpandInterface}
 * 
 * The expansion of the following planners can be controlled using the functions:
 * <ul>
 * <li>{@link StandardTrajectoryPlanner},
 * <li>{@link SimpleAnyTrajectoryPlanner},
 * <li>{@link OptimalAnyTrajectoryPlanner}
 * </ul> */
public enum Expand {
  ;
  /** @param expandInterface
   * @param expandLimit
   * @return number times function {@link ExpandInterface#expand(GlcNode)} was invoked */
  public static int maxSteps(ExpandInterface<?> expandInterface, int expandLimit) {
    return maxSteps(expandInterface, expandLimit, () -> true);
  }

  /** planner aborts if isContinued supplies false
   * 
   * @param expandInterface
   * @param expandLimit
   * @param isContinued
   * @return number times function {@link ExpandInterface#expand(GlcNode)} was invoked */
  public static <T extends StateCostNode> int maxSteps(ExpandInterface<T> expandInterface, int expandLimit, Supplier<Boolean> isContinued) {
    int expandCount = 0;
    while (expandCount < expandLimit) {
      Optional<T> next = expandInterface.pollNext();
      if (!next.isPresent()) { // queue is empty
        System.out.println("*** Queue is empty -- No Goal was found ***");
        break;
      }
      // System.out.println("expand "+next.get().stateTime().toInfoString());
      expandInterface.expand(next.get());
      ++expandCount;
      if (expandInterface.getBest().isPresent()) // found node in goal region
        break;
      if (!isContinued.get())
        break;
    }
    // no printout here, since expand limit can deliberately set to a low number for animation
    // see Se2rExpandDemo
    return expandCount;
  }

  /** expands until the time of the running algorithm exceeds the maxTime or a goal was found
   * 
   * @param expandInterface
   * @param timeLimit of expandfunction in [s]
   * @return number times function {@link ExpandInterface#expand(GlcNode)} was invoked */
  public static <T extends StateCostNode> int maxTime(ExpandInterface<T> expandInterface, Scalar timeLimit) {
    System.out.println("*** EXPANDING ***");
    Stopwatch stopwatch = Stopwatch.started();
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
        stopwatch.stop();
        System.out.println("after " + stopwatch.display_seconds() + "s");
        break;
      }
      if (time < stopwatch.display_seconds()) {
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
   * @return number times function {@link ExpandInterface#expand(GlcNode)} was invoked */
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
    // no printout here, since expand limit can deliberately set to a low number for animation
    // see Se2rExpandDemo
    return expandCount;
  }
}
