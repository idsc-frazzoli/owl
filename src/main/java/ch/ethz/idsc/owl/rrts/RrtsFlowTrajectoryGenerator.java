// code by jph, gjoel
package ch.ethz.idsc.owl.rrts;

import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.owl.data.Lists;
import ch.ethz.idsc.owl.math.StateSpaceModel;
import ch.ethz.idsc.owl.math.StateSpaceModels;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.owl.rrts.core.RrtsNode;
import ch.ethz.idsc.owl.rrts.core.Transition;
import ch.ethz.idsc.owl.rrts.core.TransitionWrap;
import ch.ethz.idsc.owl.rrts.core.TransitionSpace;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/* package */ class RrtsFlowTrajectoryGenerator {
  private final StateSpaceModel stateSpaceModel;

  public RrtsFlowTrajectoryGenerator(StateSpaceModel stateSpaceModel) {
    this.stateSpaceModel = stateSpaceModel;
  }

  /** @param transitionSpace
   * @param sequence
   * @param dt
   * @return */
  public List<TrajectorySample> createTrajectory( //
      TransitionSpace transitionSpace, List<RrtsNode> sequence, Scalar t0, final Scalar dt) {
    List<TrajectorySample> trajectory = new LinkedList<>();
    RrtsNode prev = sequence.get(0);
    for (RrtsNode node : sequence.subList(1, sequence.size())) {
      Transition transition = transitionSpace.connect(prev.state(), node.state());
      TransitionWrap transitionWrap = transition.wrapped(dt);
      Tensor samples = transitionWrap.samples();
      Tensor spacing = transitionWrap.spacing();
      Scalar ti = t0;
      for (int i = 0; i < samples.length(); i++) {
        ti = ti.add(spacing.Get(i));
        StateTime stateTime = new StateTime(samples.get(i), ti);
        if (trajectory.isEmpty())
          trajectory.add(TrajectorySample.head(stateTime));
        else {
          StateTime orig = Lists.getLast(trajectory).stateTime();
          Flow flow = between(orig, stateTime);
          trajectory.add(new TrajectorySample(stateTime, flow));
        }
      }
      prev = node;
      t0 = t0.add(transition.length());
    }
    return trajectory;
  }

  /** @param orig
   * @param dest
   * @return */
  private Flow between(StateTime orig, StateTime dest) {
    Tensor direction = dest.state().subtract(orig.state());
    Scalar delta = dest.time().subtract(orig.time());
    return StateSpaceModels.createFlow(stateSpaceModel, direction.divide(delta));
  }
}
