// code by jph
package ch.ethz.idsc.owl.glc.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.ethz.idsc.owl.data.GlobalAssert;
import ch.ethz.idsc.owl.data.Lists;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public enum Trajectories {
  ;
  /** @param head
   * @param tail
   * @return trajectory { head[0:end], tail[1:end]}
   * @throws Exception if head[end] != tail[0] */
  public static List<TrajectorySample> glue(List<TrajectorySample> head, List<TrajectorySample> tail) {
    List<TrajectorySample> trajectory = new ArrayList<>();
    trajectory.addAll(head);
    TrajectorySample tsh = Lists.getLast(head);
    TrajectorySample tst = tail.get(0);
    boolean contact = tsh.stateTime().equals(tst.stateTime());
    if (!contact) {
      System.out.println("last of head: " + tsh.toInfoString());
      System.out.println(" 1st of tail: " + tst.toInfoString());
      GlobalAssert.that(contact);
    }
    GlobalAssert.that(!tst.getFlow().isPresent());
    trajectory.addAll(tail.subList(1, tail.size()));
    return Collections.unmodifiableList(trajectory);
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

  public static void print(List<TrajectorySample> list) {
    System.out.println("Trajectory (" + list.size() + ")");
    for (TrajectorySample sample : list)
      System.out.println(sample.toInfoString());
  }
}
