// code by ynager
package ch.ethz.idsc.owl.bot.se2.glc;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import ch.ethz.idsc.owl.ani.api.GlcPlannerCallback;
import ch.ethz.idsc.owl.ani.api.TrajectoryEntity;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.gui.win.MotionPlanWorker;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public class GlcWaypointFollowing extends WaypointFollowing {
  private static final int MAX_STEPS = 5000; // magic const
  // ---
  protected final Collection<GlcPlannerCallback> glcPlannerCallbacks;
  private MotionPlanWorker motionPlanWorker = null;
  private final PlannerConstraint plannerConstraint;

  /** @param waypoints
   * @param replanningRate
   * @param entity
   * @param plannerConstraint
   * @param glcPlannerCallbacks */
  public GlcWaypointFollowing( //
      Tensor waypoints, Scalar replanningRate, TrajectoryEntity entity, //
      PlannerConstraint plannerConstraint, Collection<GlcPlannerCallback> glcPlannerCallbacks) {
    super(waypoints, replanningRate, entity);
    this.glcPlannerCallbacks = glcPlannerCallbacks;
    this.plannerConstraint = plannerConstraint;
  }

  @Override
  protected void planToGoal(List<TrajectorySample> head, Tensor goal) {
    if (Objects.nonNull(motionPlanWorker)) {
      motionPlanWorker.flagShutdown();
      motionPlanWorker = null;
    }
    motionPlanWorker = new MotionPlanWorker(MAX_STEPS, glcPlannerCallbacks);
    motionPlanWorker.start(head, entity.createTrajectoryPlanner(plannerConstraint, goal));
  }
}
