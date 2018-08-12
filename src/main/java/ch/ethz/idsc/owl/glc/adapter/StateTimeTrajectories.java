// code by jph
package ch.ethz.idsc.owl.glc.adapter;

import java.util.List;

import ch.ethz.idsc.owl.data.Lists;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Sign;

/** utility functions that operate on List<StateTime> */
public enum StateTimeTrajectories {
  ;
  /** @param glcNode
   * @param trajectory
   * @return time increment between given from State and end of trajectory
   * @throws Exception if time of last node is smaller than of given stateTime */
  public static Scalar timeIncrement(GlcNode glcNode, List<StateTime> trajectory) {
    return timeIncrement(glcNode.stateTime(), trajectory);
  }

  // helper function
  private static Scalar timeIncrement(StateTime stateTime, List<StateTime> trajectory) {
    Scalar dt = Lists.getLast(trajectory).time().subtract(stateTime.time());
    return Sign.requirePositiveOrZero(dt);
  }

  /** @param glcNode
   * @param trajectory
   * @return vector with {dt_0, dt_1, ... } all entries non-negative */
  public static Tensor deltaTimes(GlcNode glcNode, List<StateTime> trajectory) {
    Tensor dts = Tensors.empty();
    Scalar prev = glcNode.stateTime().time();
    for (StateTime stateTime : trajectory) {
      Scalar next = stateTime.time();
      dts.append(next.subtract(prev));
      prev = next;
    }
    return dts;
  }

  /** print trajectory to console
   * 
   * @param list */
  public static void print(List<StateTime> list) {
    System.out.println("Trajectory (" + list.size() + ")");
    for (StateTime stateTime : list)
      System.out.println(stateTime.toInfoString());
  }
}
