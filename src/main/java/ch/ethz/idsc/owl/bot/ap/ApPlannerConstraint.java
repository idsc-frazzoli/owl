// code by astoll
package ch.ethz.idsc.owl.bot.ap;

import java.io.Serializable;
import java.util.List;

import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Sign;
import ch.ethz.idsc.tensor.sca.Sin;

/** ApPlannerConstraint sets the constraints for the airplane simulation.
 * 
 * x and z need to be positive,
 * the velocity shall not be smaller than stall speed and not greater than the maximum speed,
 * the flight path angle (gamma) should be no less than -3 degree and always negative
 * the descent rate should be z_dot <= V * sin(gamma) in final landing phase
 * 
 * The values always are to be found in {@link ApStateSpaceModel}
 * 
 * @author Andre */
/* package */ class ApPlannerConstraint implements PlannerConstraint, Serializable {
  private static final Clip CLIP_GAMMA = //
      Clip.function(ApStateSpaceModel.MAX_DESCENT_GAMMA, ApStateSpaceModel.MAX_DESCENT_GAMMA.zero());
  private static final Clip CLIP_VELOCITY = //
      Clip.function(ApStateSpaceModel.STALL_SPEED, ApStateSpaceModel.MAX_SPEED);

  @Override // from PlannerConstraint
  public boolean isSatisfied(GlcNode glcNode, List<StateTime> trajectory, Flow flow) {
    Tensor state = glcNode.state();
    // boolean xConstraint = Sign.isPositiveOrZero(state.Get(0));
    // if (!xConstraint)
    // return false;
    Scalar z = state.Get(1);
    if (Sign.isNegative(z))
      return false;
    Scalar v = state.Get(2);
    // boolean vConstraint = Scalars.lessEquals(v, ApStateSpaceModel.MAX_SPEED)//
    // && Scalars.lessEquals(ApStateSpaceModel.STALL_SPEED, v);
    if (CLIP_VELOCITY.isOutside(v))
      return false;
    Scalar gamma = state.Get(3); // flight path angle
    if (CLIP_GAMMA.isOutside(gamma))
      return false;
    if (Scalars.lessEquals(z, ApStateSpaceModel.ALTITUDE_FINAL_PHASE)) {
      Scalar v_z = v.multiply(Sin.of(gamma));
      return Scalars.lessEquals(v_z, ApStateSpaceModel.Z_0);
    }
    return true;//
  }
}
