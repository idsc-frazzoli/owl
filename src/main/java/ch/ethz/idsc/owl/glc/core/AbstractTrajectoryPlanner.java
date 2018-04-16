// code by bapaden, jph, and jl
package ch.ethz.idsc.owl.glc.core;

import java.util.List;
import java.util.Objects;

import ch.ethz.idsc.owl.glc.std.PlannerConstraint;
import ch.ethz.idsc.owl.glc.std.StandardTrajectoryPlanner;
import ch.ethz.idsc.owl.math.state.StateIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.Tensor;

/** planner is shared between
 * {@link StandardTrajectoryPlanner} and {@link AbstractAnyTrajectoryPlanner} */
public abstract class AbstractTrajectoryPlanner extends TrajectoryPlanner {
  private final StateIntegrator stateIntegrator;
  private final PlannerConstraint plannerConstraint;
  private final GoalInterface goalInterface;

  protected AbstractTrajectoryPlanner( //
      Tensor eta, //
      StateIntegrator stateIntegrator, //
      PlannerConstraint plannerConstraint, //
      GoalInterface goalInterface) {
    super(eta);
    this.stateIntegrator = stateIntegrator;
    this.plannerConstraint = Objects.requireNonNull(plannerConstraint);
    this.goalInterface = goalInterface;
  }

  @Override // from TrajectoryPlanner
  public final StateIntegrator getStateIntegrator() {
    return stateIntegrator;
  }

  @Override // from TrajectoryPlanner
  public final PlannerConstraint getPlannerConstraint() {
    return plannerConstraint;
  }

  @Override // from TrajectoryPlanner
  public final GoalInterface getGoalInterface() {
    return goalInterface;
  }

  protected final boolean isInsideGoal(List<StateTime> trajectory) {
    return goalInterface.firstMember(trajectory).isPresent();
  }
}
