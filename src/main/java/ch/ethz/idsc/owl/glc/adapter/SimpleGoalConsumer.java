// code by jph
package ch.ethz.idsc.owl.glc.adapter;

import java.util.Collection;
import java.util.Objects;

import ch.ethz.idsc.owl.glc.core.GoalConsumer;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.gui.ani.GlcPlannerCallback;
import ch.ethz.idsc.owl.gui.ani.TrajectoryEntity;
import ch.ethz.idsc.owl.gui.win.MotionPlanWorker;
import ch.ethz.idsc.tensor.Tensor;

public class SimpleGoalConsumer implements GoalConsumer {
  private static final int MAX_STEPS = 5000; // magic const
  // ---
  private final TrajectoryEntity trajectoryEntity;
  private final PlannerConstraint plannerConstraint;
  private final Collection<GlcPlannerCallback> glcPlannerCallbacks;
  // ---
  private MotionPlanWorker motionPlanWorker = null;

  public SimpleGoalConsumer(TrajectoryEntity trajectoryEntity, PlannerConstraint plannerConstraint, Collection<GlcPlannerCallback> glcPlannerCallbacks) {
    this.trajectoryEntity = trajectoryEntity;
    this.plannerConstraint = plannerConstraint;
    this.glcPlannerCallbacks = glcPlannerCallbacks;
  }

  @Override
  public void accept(Tensor goal) {
    // System.out.println("goal: " + goal);
    if (Objects.nonNull(motionPlanWorker)) {
      motionPlanWorker.flagShutdown();
      motionPlanWorker = null;
    }
    motionPlanWorker = new MotionPlanWorker(MAX_STEPS, glcPlannerCallbacks);
    motionPlanWorker.start( //
        trajectoryEntity.getFutureTrajectoryUntil(trajectoryEntity.delayHint()), //
        trajectoryEntity.createTrajectoryPlanner(plannerConstraint, goal));
  }
}
