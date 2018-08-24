package ch.ethz.idsc.owl.bot.tse2;

import java.util.List;

import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;

/** Velocity constraint for Tse2 state space */
public class Tse2VelocityConstraint implements PlannerConstraint {
  Scalar min;
  Scalar max;

  /** @param min min allowed velocity
   * @param max max allowed velocity */
  public Tse2VelocityConstraint(Scalar min, Scalar max) {
    this.min = min;
    this.max = max;
  }

  @Override
  public boolean isSatisfied(GlcNode glcNode, List<StateTime> trajectory, Flow flow) {
    return !trajectory.stream() //
        .map(StateTime::state) //
        .map(st -> st.Get(3)).anyMatch(s -> (Scalars.lessThan(s, min) || Scalars.lessThan(max, s)));
  }
}
