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
 * The values always are to be found in {Link @ApStateSpaceModel}
 * 
 * @author Andre */
/* package */ class ApPlannerConstraint implements PlannerConstraint, Serializable {
  static final Clip CLIP_GAMMA = Clip.function(ApStateSpaceModel.MAX_DESCENT_GAMMA, ApStateSpaceModel.MAX_DESCENT_GAMMA.zero());

  @Override
  public boolean isSatisfied(GlcNode glcNode, List<StateTime> trajectory, Flow flow) {
    boolean xConstraint = Sign.isPositiveOrZero(glcNode.state().Get(0));
    if (!xConstraint) {
      return false;
    }
    boolean zConstraint = Sign.isPositiveOrZero(glcNode.state().Get(1));
    if (!zConstraint) {
      return false;
    }
    boolean vConstraint = Scalars.lessEquals(glcNode.state().Get(2), ApStateSpaceModel.MAX_SPEED)//
        && Scalars.lessEquals(ApStateSpaceModel.STALL_SPEED, glcNode.state().Get(2));
    if (!vConstraint) {
      return false;
    }
    Scalar gamma = glcNode.state().Get(3);
    boolean gammaConstraint = CLIP_GAMMA.isInside(gamma); // Scalars.lessEquals( ,) && Scalars.lessEquals(glcNode.state().Get(3),
                                                          // ApStateSpaceModel.MAX_DESCENT_GAMMA.zero());
    if (!gammaConstraint) {
      return false;
    }
    if (Scalars.lessEquals(glcNode.state().Get(1), ApStateSpaceModel.ALTITUDE_FINAL_PHASE)) {
      return Scalars.lessEquals(ApStateSpaceModel.Z_0, glcNode.state().Get(2).multiply(Sin.of(glcNode.state().Get(3))));
    }
    return true;//
  }
}
