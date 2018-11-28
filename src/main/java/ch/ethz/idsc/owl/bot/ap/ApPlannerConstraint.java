// code by astoll
package ch.ethz.idsc.owl.bot.ap;

import java.io.Serializable;
import java.util.List;

import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.sca.Sign;

/* package */ class ApPlannerConstraint implements PlannerConstraint, Serializable {
  @Override
  public boolean isSatisfied(GlcNode glcNode, List<StateTime> trajectory, Flow flow) {
    boolean xConstraint = Sign.isPositiveOrZero(glcNode.state().Get(0));
    boolean zConstraint = Sign.isPositiveOrZero(glcNode.state().Get(1));
    boolean vConstrait = Scalars.lessEquals(glcNode.state().Get(2), ApStateSpaceModel.MAX_SPEED)//
        && Scalars.lessEquals(ApStateSpaceModel.STALL_SPEED, glcNode.state().Get(2));
    // TODO gamma > -3 degree and gamma <= 0 degree
    return xConstraint && zConstraint && vConstrait;//
  }

  public static void main(String[] args) {
    Scalar s = ApStateSpaceModel.MAX_AOA;
    Scalar test = RealScalar.of(5);
    boolean i = Scalars.lessEquals(test, RealScalar.of(6));
    System.out.println(i);
  }
}
