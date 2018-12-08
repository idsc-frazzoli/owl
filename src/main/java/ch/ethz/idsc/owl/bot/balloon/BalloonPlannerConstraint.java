// code by astoll
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
  /** constants of the hot air balloon */
  private final Clip vertSpeed_clip;

  public BalloonPlannerConstraint(Scalar vertSpeedMax) {
    vertSpeed_clip = Clip.function(vertSpeedMax.negate(), vertSpeedMax);
  }

  @Override // from PlannerConstraint
  public boolean isSatisfied(GlcNode glcNode, List<StateTime> trajectory, Flow flow) {
    Tensor state = glcNode.state();
    // boolean xConstraint = Sign.isPositiveOrZero(state.Get(0));
    // if (!xConstraint)
    // return false;
    Scalar y = state.Get(1); // altitude
    if (Sign.isNegative(y))
      return false;
    Scalar v = state.Get(2); // vertical speed
    if (vertSpeed_clip.isOutside(v))
      return false;
    /* not quite sure if this is a valid constraint
     * Scalar theta = state.Get(3); // f
     * if (Sign.isNegative(theta))
     * return false; */
    return true;//
  }
}
