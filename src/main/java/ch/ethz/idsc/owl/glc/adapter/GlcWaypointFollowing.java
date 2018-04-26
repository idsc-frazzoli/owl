// code by ynager
package ch.ethz.idsc.owl.glc.adapter;

import java.util.List;
import java.util.Objects;

import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.glc.std.PlannerConstraint;
import ch.ethz.idsc.owl.gui.ani.TrajectoryEntity;
import ch.ethz.idsc.owl.gui.ani.TrajectoryPlannerCallback;
import ch.ethz.idsc.owl.gui.win.MotionPlanWorker;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public class GlcWaypointFollowing extends WaypointFollowing {
  protected final TrajectoryPlannerCallback trajectoryPlannerCallback;
  private MotionPlanWorker mpw = null;

  /** @param waypoints
   * @param entity
   * @param plannerConstraint
   * @param trajectoryPlannerCallback */
  public GlcWaypointFollowing( //
      Tensor waypoints, //
      Scalar replanningRate, //
      TrajectoryEntity entity, //
      PlannerConstraint plannerConstraint, //
      TrajectoryPlannerCallback trajectoryPlannerCallback) {
    super(waypoints, replanningRate, entity, plannerConstraint);
    this.trajectoryPlannerCallback = trajectoryPlannerCallback;
  }

  @Override
  protected void planToGoal(List<TrajectorySample> head, Tensor goal) {
    if (Objects.nonNull(mpw)) {
      mpw.flagShutdown();
      mpw = null;
    }
    TrajectoryPlanner trajectoryPlanner = entity.createTrajectoryPlanner(plannerConstraint, goal);
    mpw = new MotionPlanWorker();
    mpw.addCallback(trajectoryPlannerCallback);
    mpw.start(head, trajectoryPlanner);
  }
}
