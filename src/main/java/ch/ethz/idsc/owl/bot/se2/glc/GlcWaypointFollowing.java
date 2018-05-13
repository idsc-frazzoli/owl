// code by ynager
package ch.ethz.idsc.owl.bot.se2.glc;

import java.util.List;
import java.util.Objects;

import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.glc.std.PlannerConstraint;
import ch.ethz.idsc.owl.gui.ani.GlcPlannerCallback;
import ch.ethz.idsc.owl.gui.ani.TrajectoryEntity;
import ch.ethz.idsc.owl.gui.win.MotionPlanWorker;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public class GlcWaypointFollowing extends WaypointFollowing {
  protected final GlcPlannerCallback glcPlannerCallback;
  private MotionPlanWorker motionPlanWorker = null;
  private final PlannerConstraint plannerConstraint;

  /** @param waypoints
   * @param replanningRate
   * @param entity
   * @param plannerConstraint
   * @param glcPlannerCallback */
  public GlcWaypointFollowing( //
      Tensor waypoints, Scalar replanningRate, TrajectoryEntity entity, //
      PlannerConstraint plannerConstraint, GlcPlannerCallback glcPlannerCallback) {
    super(waypoints, replanningRate, entity);
    this.glcPlannerCallback = glcPlannerCallback;
    this.plannerConstraint = plannerConstraint;
  }

  @Override
  protected void planToGoal(List<TrajectorySample> head, Tensor goal) {
    if (Objects.nonNull(motionPlanWorker)) {
      motionPlanWorker.flagShutdown();
      motionPlanWorker = null;
    }
    TrajectoryPlanner trajectoryPlanner = entity.createTrajectoryPlanner(plannerConstraint, goal);
    motionPlanWorker = new MotionPlanWorker();
    motionPlanWorker.addCallback(glcPlannerCallback);
    motionPlanWorker.start(head, trajectoryPlanner);
  }
}
