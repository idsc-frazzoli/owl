// code by jl
package ch.ethz.idsc.owl.glc.adapter;

import java.util.Optional;

import ch.ethz.idsc.owl.data.Stopwatch;
import ch.ethz.idsc.owl.glc.core.ExpandInterface;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.tensor.Scalar;

/** expand function that apply to ExpandInterface<GlcNode>
 * for instance because they inspect the depth of a GlcNode */
public enum GlcExpand {
  ;
  /** expands until the depth of the polled node exceeds given depthLimit
   * 
   * @param expandInterface
   * @param depthLimit
   * @return number times function {@link ExpandInterface#expand(GlcNode)} was invoked */
  public static int maxDepth(ExpandInterface<GlcNode> expandInterface, int depthLimit) {
    System.out.println("*** EXPANDING ***");
    int expandCount = 0;
    while (true) {
      Optional<GlcNode> next = expandInterface.pollNext();
      if (!next.isPresent()) { // queue is empty
        System.err.println("**** Queue is empty -- No Goal was found");// queue is empty
        break;
      }
      expandInterface.expand(next.get());
      ++expandCount;
      if (expandInterface.getBest().isPresent()) // found node in goal region
        break;
      if (depthLimit < next.get().depth()) {
        System.err.println("*** DepthLimit reached -- No Goal was found ***");
        break;
      }
    }
    return expandCount;
  }

  /** total number of expands are bounded by expandLimit & depthLimit
   * 
   * @param expandInterface
   * @param expandLimit
   * @param depthLimit
   * @return number times function {@link ExpandInterface#expand(GlcNode)} was invoked */
  public static int maxSteps(ExpandInterface<GlcNode> expandInterface, int expandLimit, int depthLimit) {
    int expandCount = 0;
    while (expandCount < expandLimit) {
      Optional<GlcNode> next = expandInterface.pollNext();
      if (!next.isPresent()) { // queue is empty
        System.out.println("*** Queue is empty -- No Goal was found ***");
        break;
      }
      expandInterface.expand(next.get());
      ++expandCount;
      if (expandInterface.getBest().isPresent()) // found node in goal region
        break;
      if (depthLimit < next.get().depth()) {
        System.err.println("*** DepthLimit reached -- No Goal was found ***");
        break;
      }
    }
    // no printout here, since expand limit can deliberately set to a low number for animation
    // see Se2rExpandDemo
    return expandCount;
  }

  /** expands until the time of the running algorithm exceeds time or depthlimit is reached
   * 
   * @param expandInterface
   * @param time Time of expandfunction in [s]
   * @return number times function {@link ExpandInterface#expand(GlcNode)} was invoked */
  public static int constTime(ExpandInterface<GlcNode> expandInterface, Scalar _time, int depthLimit) {
    System.out.println("*** EXPANDING ***");
    Stopwatch stopwatch = Stopwatch.started();
    final double time = _time.number().doubleValue();
    int expandCount = 0;
    while (true) {
      Optional<GlcNode> next = expandInterface.pollNext();
      if (!next.isPresent()) {
        System.err.println("**** Queue is empty ****"); // queue is empty
        break;
      }
      expandInterface.expand(next.get());
      ++expandCount;
      if (time < stopwatch.display_seconds()) {
        System.out.println("***Planned for " + _time + "s ***");
        break;
      }
      if (depthLimit < next.get().depth()) {
        System.err.println("*** DepthLimit reached -- No Goal was found ***");
        break;
      }
    }
    return expandCount;
  }
}
