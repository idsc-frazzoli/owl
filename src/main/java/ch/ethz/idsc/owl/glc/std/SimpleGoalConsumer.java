// code by jph
package ch.ethz.idsc.owl.glc.std;

import java.util.List;
import java.util.Objects;

import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.gui.ani.GlcPlannerCallback;
import ch.ethz.idsc.owl.gui.ani.TrajectoryEntity;
import ch.ethz.idsc.owl.gui.win.MotionPlanWorker;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.tensor.Tensor;

public class SimpleGoalConsumer implements GoalConsumer {
  private final TrajectoryEntity trajectoryEntity;
  private final PlannerConstraint plannerConstraint;
  private final GlcPlannerCallback glcPlannerCallback;
  private MotionPlanWorker motionPlanWorker = null;

  public SimpleGoalConsumer(TrajectoryEntity trajectoryEntity, PlannerConstraint plannerConstraint, GlcPlannerCallback glcPlannerCallback) {
    this.trajectoryEntity = trajectoryEntity;
    this.plannerConstraint = plannerConstraint;
    this.glcPlannerCallback = glcPlannerCallback;
  }

  @Override
  public void accept(Tensor goal) {
    // System.out.println("goal: " + goal);
    if (Objects.nonNull(motionPlanWorker)) {
      motionPlanWorker.flagShutdown();
      motionPlanWorker = null;
    }
    final List<TrajectorySample> head = //
        trajectoryEntity.getFutureTrajectoryUntil(trajectoryEntity.delayHint());
    TrajectoryPlanner trajectoryPlanner = //
        trajectoryEntity.createTrajectoryPlanner(plannerConstraint, goal);
    motionPlanWorker = new MotionPlanWorker();
    motionPlanWorker.addCallback(glcPlannerCallback);
    if (trajectoryEntity instanceof GlcPlannerCallback)
      motionPlanWorker.addCallback((GlcPlannerCallback) trajectoryEntity);
    motionPlanWorker.start(head, trajectoryPlanner);
  }
}
