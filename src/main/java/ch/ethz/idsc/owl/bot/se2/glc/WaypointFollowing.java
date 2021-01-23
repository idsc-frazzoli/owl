// code by ynager
package ch.ethz.idsc.owl.bot.se2.glc;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ch.ethz.idsc.owl.ani.api.TrajectoryEntity;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Sign;

public abstract class WaypointFollowing {
  private final Tensor waypoints;
  private final Scalar replanningRate;
  protected final TrajectoryEntity entity;
  private Scalar horizonDistance = DoubleScalar.POSITIVE_INFINITY;
  private Timer timer;

  /** @param waypoints to be followed
   * @param replanningRate
   * @param entity */
  public WaypointFollowing(Tensor waypoints, Scalar replanningRate, TrajectoryEntity entity) {
    this.waypoints = waypoints.unmodifiable();
    this.entity = entity;
    this.replanningRate = Sign.requirePositive(replanningRate);
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
    TimerTask timerTask = new TimerTask() {
      int i = 0; // TODO_YN find generic seed

      @Override
      public void run() {
        Scalar planningDelay = replanningRate.reciprocal();
        Tensor loc = entity.getEstimatedLocationAt(planningDelay); // get location from previous trajectory
        // ---
        Tensor goal = waypoints.get(i);
        while (Scalars.lessThan(entity.distance(loc, goal), horizonDistance)) {
          ++i;
          i %= waypoints.length();
          goal = waypoints.get(i);
        }
        List<TrajectorySample> head = entity.getFutureTrajectoryUntil(planningDelay);
        planToGoal(head, goal);
      }
    };
    timer = new Timer("PlanningTimer");
    int period = 1000 / replanningRate.number().intValue();
    // System.out.println("period="+period);
    timer.schedule(timerTask, 10, period);
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
