// code by ynager
package ch.ethz.idsc.owl.glc.adapter;

import java.util.List;
import java.util.Objects;

import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.glc.std.PlannerConstraint;
import ch.ethz.idsc.owl.gui.ani.TrajectoryEntity;
import ch.ethz.idsc.owl.gui.win.MotionPlanWorker;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.tensor.Tensor;

public class GlcWaypointFollowing extends WaypointFollowing {
  private MotionPlanWorker mpw = null;

  /** @param waypoints
   * @param entity
   * @param trajectoryPlannerCallback
   * @param obstacleQuery non-null */
  public GlcWaypointFollowing( //
      Tensor waypoints, TrajectoryEntity entity, PlannerConstraint plannerConstraint) {
    super(waypoints, entity, plannerConstraint);
  }

  @Override
  protected void planToGoal(List<TrajectorySample> head, Tensor goal) {
    if (Objects.nonNull(mpw)) {
      mpw.flagShutdown();
      mpw = null;
    }
    TrajectoryPlanner trajectoryPlanner = trajEntity.createTrajectoryPlanner(plannerConstraint, goal);
    mpw = new MotionPlanWorker();
    mpw.start(head, trajectoryPlanner);
  }
}
