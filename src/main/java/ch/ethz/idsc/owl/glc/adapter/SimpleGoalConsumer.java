// code by jph
package ch.ethz.idsc.owl.glc.adapter;

import java.util.Collection;
import java.util.Objects;

import ch.ethz.idsc.owl.ani.api.GlcPlannerCallback;
import ch.ethz.idsc.owl.ani.api.TrajectoryEntity;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.gui.win.GlcMotionPlanWorker;
import ch.ethz.idsc.tensor.Tensor;

public class SimpleGoalConsumer implements GoalConsumer {
  private static final int MAX_STEPS = 5000; // magic const
  // ---
  private final TrajectoryEntity trajectoryEntity;
  private final PlannerConstraint plannerConstraint;
  private final Collection<GlcPlannerCallback> glcPlannerCallbacks;
  // ---
  private GlcMotionPlanWorker motionPlanWorker = null;

  public SimpleGoalConsumer(TrajectoryEntity trajectoryEntity, PlannerConstraint plannerConstraint, Collection<GlcPlannerCallback> glcPlannerCallbacks) {
    this.trajectoryEntity = trajectoryEntity;
    this.plannerConstraint = plannerConstraint;
    this.glcPlannerCallbacks = glcPlannerCallbacks;
  }

  @Override // from GoalConsumer
  public void accept(Tensor goal) {
    if (Objects.nonNull(motionPlanWorker)) {
      motionPlanWorker.flagShutdown();
      motionPlanWorker = null;
    }
    motionPlanWorker = new GlcMotionPlanWorker(MAX_STEPS, glcPlannerCallbacks);
    motionPlanWorker.start( //
        trajectoryEntity.getFutureTrajectoryUntil(trajectoryEntity.delayHint()), //
        trajectoryEntity.createTrajectoryPlanner(plannerConstraint, goal));
  }
}
