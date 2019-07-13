// code by gjoel
package ch.ethz.idsc.owl.rrts.adapter;

import java.util.Collection;

import ch.ethz.idsc.owl.ani.api.PlannerCallback;
import ch.ethz.idsc.owl.ani.api.TrajectoryEntity;
import ch.ethz.idsc.owl.data.tree.TreePlanner;
import ch.ethz.idsc.owl.glc.adapter.GoalConsumer;
import ch.ethz.idsc.owl.glc.adapter.SimpleGoalConsumer;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.math.Lane;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Last;

public class SimpleLaneConsumer implements LaneConsumer {
  private final TrajectoryEntity trajectoryEntity;
  private final PlannerConstraint plannerConstraint;
  private final GoalConsumer goalConsumer;

  public SimpleLaneConsumer(TrajectoryEntity trajectoryEntity, PlannerConstraint plannerConstraint, Collection<? extends PlannerCallback> plannerCallbacks) {
    this.trajectoryEntity = trajectoryEntity;
    this.plannerConstraint = plannerConstraint;
    goalConsumer = new SimpleGoalConsumer(trajectoryEntity, plannerConstraint, plannerCallbacks);
  }

  @Override // from Consumer
  public void accept(Lane lane) {
    Tensor goal = Last.of(lane.midLane());
    TreePlanner treePlanner = trajectoryEntity.createTreePlanner(plannerConstraint, goal);
    if (treePlanner instanceof LaneConsumer)
      ((LaneConsumer) treePlanner).accept(lane);
    goalConsumer.accept(goal);
  }
}
