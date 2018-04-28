// code by ynager
package ch.ethz.idsc.owl.bot.se2.glc;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import ch.ethz.idsc.owl.glc.std.PlannerConstraint;
import ch.ethz.idsc.owl.gui.ani.TrajectoryEntity;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Sign;

public abstract class WaypointFollowing {
  private final Scalar replanningRate;
  protected final PlannerConstraint plannerConstraint;
  protected final TrajectoryEntity entity;
  private final Tensor waypoints;
  private Scalar horizonDistance = DoubleScalar.POSITIVE_INFINITY;
  private List<TrajectorySample> head;
  private Tensor goal;
  private Timer timer;
  private int i = 0;

  /** @param waypoints to be followed
   * @param replanningRate
   * @param entity
   * @param plannerConstraint */
  public WaypointFollowing(Tensor waypoints, Scalar replanningRate, TrajectoryEntity entity, PlannerConstraint plannerConstraint) {
    this.waypoints = waypoints;
    this.entity = entity;
    this.replanningRate = Sign.requirePositive(replanningRate);
    this.plannerConstraint = Objects.requireNonNull(plannerConstraint);
  }

  /** Sets the horizon distance. The planner will chose a waypoint to plan to whose distance is
   * at least horizonDistance away from the current location plus distance traveled during planning.
   * The distance measure is defined by the TrajectoryEntity.
   * 
   * @param horizonDistance */
  public final void setHorizonDistance(Scalar horizonDistance) {
    this.horizonDistance = horizonDistance;
  }

  /** start planning through waypoints */
  public final void startNonBlocking() {
    goal = waypoints.get(i);
    // ---
    TimerTask timerTask = new TimerTask() {
      @Override
      public void run() {
        Scalar planningDelay = replanningRate.reciprocal();
        Tensor loc = entity.getEstimatedLocationAt(planningDelay); // get location from previous trajectory
        // ---
        while (Scalars.lessThan(entity.distance(loc, goal), horizonDistance) || // loop until suitable goal is found
        !plannerConstraint.isSatisfied(null, Arrays.asList(new StateTime(goal, RealScalar.ZERO)), null)) { // FIXME
          i = (i + 1) % waypoints.length();
          goal = waypoints.get(i);
        }
        head = entity.getFutureTrajectoryUntil(planningDelay);
        planToGoal(head, goal);
      }
    };
    timer = new Timer("PlanningTimer");
    timer.schedule(timerTask, 10, 1000 / replanningRate.number().intValue());
  }

  public final void flagShutdown() {
    timer.cancel();
  }

  /** starts planning towards a goal
   * 
   * @param TrajectoryPlannerCallback
   * @param head
   * @param goal */
  protected abstract void planToGoal(List<TrajectorySample> head, Tensor goal);
}
