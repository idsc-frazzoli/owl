// code by ynager
package ch.ethz.idsc.owl.bot.tse2;

import java.io.Serializable;
import java.util.List;

import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Clip;

/** Velocity constraint for {@link Tse2StateSpaceModel} */
public class Tse2VelocityConstraint implements PlannerConstraint, Serializable {
  private final Clip clip;

  /** @param min allowed velocity
   * @param max allowed velocity */
  public Tse2VelocityConstraint(Scalar min, Scalar max) {
    clip = Clip.function(min, max);
  }

  @Override // from PlannerConstraint
  public boolean isSatisfied(GlcNode glcNode, List<StateTime> trajectory, Flow flow) {
    return trajectory.stream() //
        .map(StateTime::state) //
        .map(Tse2StateSpaceModel.STATE_VELOCITY) //
        .allMatch(clip::isInside);
  }
}
