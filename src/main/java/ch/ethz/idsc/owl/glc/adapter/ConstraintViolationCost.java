// code by ynager
package ch.ethz.idsc.owl.glc.adapter;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import ch.ethz.idsc.owl.glc.core.CostFunction;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** Transforms a {@link PlannerConstraint} to a {@link CostFunction} by counting
 * constraint violations */
public class ConstraintViolationCost implements CostFunction, Serializable {
  public static ConstraintViolationCost of(PlannerConstraint plannerConstraint) {
    return new ConstraintViolationCost(plannerConstraint);
  }

  // ---
  private final PlannerConstraint plannerConstraint;

  public ConstraintViolationCost(PlannerConstraint plannerConstraint) {
    this.plannerConstraint = Objects.requireNonNull(plannerConstraint);
  }

  @Override // from CostIncrementFunction
  public Scalar costIncrement(GlcNode glcNode, List<StateTime> trajectory, Flow flow) {
    return plannerConstraint.isSatisfied(glcNode, trajectory, flow) ? RealScalar.ZERO : RealScalar.ONE;
  }

  @Override // from HeuristicFunction
  public Scalar minCostToGoal(Tensor x) {
    return RealScalar.ZERO;
  }
}
