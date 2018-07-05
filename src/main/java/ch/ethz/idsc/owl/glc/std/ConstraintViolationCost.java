// code by ynager
package ch.ethz.idsc.owl.glc.std;

import java.io.Serializable;
import java.util.List;

import ch.ethz.idsc.owl.glc.core.CostFunction;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** 
 * Transforms a @PlannerConstraint to a @CostFunction by counting constraint violations
 */
public class ConstraintViolationCost implements CostFunction, Serializable {
  private final PlannerConstraint constraint;

  public ConstraintViolationCost(PlannerConstraint constraint) {
    this.constraint = constraint;
  }

  @Override
  public Scalar minCostToGoal(Tensor x) {
    return RealScalar.ZERO;
  }

  @Override
  public Scalar costIncrement(GlcNode glcNode, List<StateTime> trajectory, Flow flow) {
    return constraint.isSatisfied(glcNode, trajectory, flow) ? RealScalar.ZERO : RealScalar.ONE;
  }
}
