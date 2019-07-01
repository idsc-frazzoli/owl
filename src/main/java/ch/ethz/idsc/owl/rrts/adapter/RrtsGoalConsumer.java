// code by jph, gjoel
package ch.ethz.idsc.owl.rrts.adapter;

import java.util.Collection;
import java.util.Objects;

import ch.ethz.idsc.owl.ani.api.RrtsPlannerCallback;
import ch.ethz.idsc.owl.ani.api.TrajectoryEntity;
import ch.ethz.idsc.owl.glc.adapter.GoalConsumer;
import ch.ethz.idsc.owl.glc.adapter.SimpleGoalConsumer;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.gui.win.RrtsMotionPlanWorker;
import ch.ethz.idsc.tensor.Tensor;

/** TODO combine with {@link SimpleGoalConsumer} */
public class RrtsGoalConsumer implements GoalConsumer {
  private static final int MAX_STEPS = 200; // magic const
  // ---
  private final TrajectoryEntity trajectoryEntity;
  private final PlannerConstraint plannerConstraint;
  private final Collection<RrtsPlannerCallback> rrtsPlannerCallbacks;
  // ---
  private RrtsMotionPlanWorker motionPlanWorker = null;

  public RrtsGoalConsumer(TrajectoryEntity trajectoryEntity, PlannerConstraint plannerConstraint, Collection<RrtsPlannerCallback> rrtsPlannerCallbacks) {
    this.trajectoryEntity = trajectoryEntity;
    this.plannerConstraint = plannerConstraint;
    this.rrtsPlannerCallbacks = rrtsPlannerCallbacks;
  }

  @Override // from GoalConsumer
  public void accept(Tensor goal) {
    if (Objects.nonNull(motionPlanWorker)) {
      motionPlanWorker.flagShutdown();
      motionPlanWorker = null;
    }
    motionPlanWorker = new RrtsMotionPlanWorker(MAX_STEPS, rrtsPlannerCallbacks);
    motionPlanWorker.start( //
        trajectoryEntity.getFutureTrajectoryUntil(trajectoryEntity.delayHint()), //
        null /* trajectoryEntity.createTrajectoryPlanner(plannerConstraint, goal) */ ); // TODO refactor TrajectoryEntity
  }
}
