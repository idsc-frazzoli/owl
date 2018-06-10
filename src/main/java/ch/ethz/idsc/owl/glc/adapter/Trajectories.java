// code by jph
package ch.ethz.idsc.owl.glc.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import ch.ethz.idsc.owl.data.GlobalAssert;
import ch.ethz.idsc.owl.data.Lists;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;

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
      System.err.println("last of head: " + tsh.toInfoString());
      System.err.println(" 1st of tail: " + tst.toInfoString());
      // GlobalAssert.that(contact);
    }
    GlobalAssert.that(!tst.getFlow().isPresent());
    trajectory.addAll(tail.subList(1, tail.size()));
    return Collections.unmodifiableList(trajectory);
  }

  /** @param list */
  public static void print(List<TrajectorySample> list) {
    System.out.println("Trajectory (" + list.size() + ")");
    for (TrajectorySample sample : list)
      System.out.println(sample.toInfoString());
  }

  /** @param time
   * @return predicate for {@link TrajectorySample} before or at given time */
  public static Predicate<TrajectorySample> untilTime(Scalar time) {
    return trajectorySample -> Scalars.lessEquals(trajectorySample.stateTime().time(), time);
  }
}
