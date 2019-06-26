// code by jph
package ch.ethz.idsc.owl.bot.rn.rrts;

import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.owl.data.Lists;
import ch.ethz.idsc.owl.math.SingleIntegratorStateSpaceModel;
import ch.ethz.idsc.owl.math.StateSpaceModel;
import ch.ethz.idsc.owl.math.StateSpaceModels;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.owl.rrts.core.RrtsNode;
import ch.ethz.idsc.owl.rrts.core.Transition;
import ch.ethz.idsc.owl.rrts.core.TransitionSpace;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/* package */ enum RnFlowTrajectory {
  ;
  /** implementation produces shortcuts at corners.
   * the flow of the segment bridging a corner may be of smaller magnitude than
   * the flows along a single segment.
   * 
   * @param transitionSpace
   * @param sequence
   * @param dt
   * @return */
  // FIXME JPH
  public static List<TrajectorySample> createTrajectory( //
      TransitionSpace transitionSpace, List<RrtsNode> sequence, Scalar t0, final Scalar dt) {
    List<TrajectorySample> trajectory = new LinkedList<>();
    Scalar ofs = dt;
    RrtsNode prev = sequence.get(0);
    trajectory.add(TrajectorySample.head(new StateTime(prev.state(), t0)));
    // create evenly spaced trajectory samples on the trajectory described by the rrts nodes in sequence
    // is this necessary this way? implementation could be way easier by spreading samples section wise
    for (RrtsNode node : sequence.subList(1, sequence.size())) {
      // System.out.println(node.state());
      Transition transition = transitionSpace.connect(prev.state(), node.state());
      Tensor stateTimes = transition.sampled(ofs, dt);
      Scalar t = t0.add(ofs);
      for (Tensor stateTime_ : stateTimes) {
        StateTime stateTime = new StateTime(stateTime_, t);
        StateTime orig = Lists.getLast(trajectory).stateTime();
        Flow flow = between(orig, stateTime);
        trajectory.add(new TrajectorySample(stateTime, flow));
        t = t.add(dt);
      }
      prev = node;
      t0 = t0.add(transition.length());
      Scalar rem = t0.subtract(Lists.getLast(trajectory).stateTime().time());
      ofs = dt.subtract(rem);
    }
    return trajectory;
  }

  /** @param orig
   * @param dest
   * @return */
  /* package */ static Flow between(StateTime orig, StateTime dest) {
    StateSpaceModel stateSpaceModel = SingleIntegratorStateSpaceModel.INSTANCE; // R^n
    Tensor direction = dest.state().subtract(orig.state());
    Scalar delta = dest.time().subtract(orig.time());
    return StateSpaceModels.createFlow(stateSpaceModel, direction.divide(delta));
  }
}
