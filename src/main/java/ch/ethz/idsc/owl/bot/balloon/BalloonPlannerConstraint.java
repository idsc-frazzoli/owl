//code by astoll
package ch.ethz.idsc.owl.bot.balloon;

import java.io.Serializable;
import java.util.List;

import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Sign;

/* package */ class BalloonPlannerConstraint implements PlannerConstraint, Serializable {
  private static final Clip CLIP_VELOCITY = //
      Clip.function(BalloonStateSpaceModel.MAX_VERT_SPEED.zero(), BalloonStateSpaceModel.MAX_VERT_SPEED);

  @Override // from PlannerConstraint
  public boolean isSatisfied(GlcNode glcNode, List<StateTime> trajectory, Flow flow) {
    Tensor state = glcNode.state();
    // boolean xConstraint = Sign.isPositiveOrZero(state.Get(0));
    // if (!xConstraint)
    // return false;
    Scalar y = state.Get(1);
    if (Sign.isNegative(y))
      return false;
    Scalar v = state.Get(2);
    if (CLIP_VELOCITY.isOutside(v))
      return false;
    /* not quite sure if this is a valid constraint
     * Scalar theta = state.Get(3); // f
     * if (Sign.isNegative(theta))
     * return false; */
    return true;//
  }
}
