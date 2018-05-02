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
  private MotionPlanWorker mpw = null;

  public SimpleGoalConsumer(TrajectoryEntity trajectoryEntity, PlannerConstraint plannerConstraint, GlcPlannerCallback glcPlannerCallback) {
    this.trajectoryEntity = trajectoryEntity;
    this.plannerConstraint = plannerConstraint;
    this.glcPlannerCallback = glcPlannerCallback;
  }

  @Override
  public void accept(Tensor goal) {
    // System.out.println("goal: " + goal);
    if (Objects.nonNull(mpw)) {
      mpw.flagShutdown();
      mpw = null;
    }
    final List<TrajectorySample> head = //
        trajectoryEntity.getFutureTrajectoryUntil(trajectoryEntity.delayHint());
    TrajectoryPlanner trajectoryPlanner = //
        trajectoryEntity.createTrajectoryPlanner(plannerConstraint, goal);
    mpw = new MotionPlanWorker();
    mpw.addCallback(glcPlannerCallback);
    mpw.start(head, trajectoryPlanner);
  }
}
