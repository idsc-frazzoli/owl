// code by ynager
package ch.ethz.idsc.owl.glc.adapter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import ch.ethz.idsc.owl.glc.std.PlannerConstraint;
import ch.ethz.idsc.owl.gui.ani.TrajectoryEntity;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;

public abstract class WaypointFollowing {
  protected final PlannerConstraint plannerConstraint;
  protected final TrajectoryEntity trajEntity;
  private final Tensor waypoints;
  private Scalar distThreshold = DoubleScalar.POSITIVE_INFINITY;
  private boolean isRunning = true;

  public WaypointFollowing(Tensor waypoints, TrajectoryEntity entity, PlannerConstraint plannerConstraint) {
    this.waypoints = waypoints;
    this.trajEntity = entity;
    this.plannerConstraint = Objects.requireNonNull(plannerConstraint);
  }

  /** sets the distance threshold. When the distance from the current state
   * to the current goal is below this threshold, planning to the next goal
   * is initiated
   * 
   * @param distThreshold */
  public final void setDistanceThreshold(Scalar distThreshold) {
    this.distThreshold = distThreshold;
  }

  public final void flagShutdown() {
    isRunning = false;
  }

  /** start planning through waypoints */
  public final void startNonBlocking() {
    Thread thread = new Thread(new Runnable() {
      @Override
      public void run() {
        List<TrajectorySample> head = trajEntity.getFutureTrajectoryUntil(trajEntity.delayHint());
        // start waypoint tracking loop
        int i = 0;
        Tensor goal = waypoints.get(i);
        boolean init = true;
        while (isRunning) {
          Tensor loc = trajEntity.getEstimatedLocationAt(trajEntity.delayHint());
          Scalar dist = trajEntity.distance(loc, goal);
          //
          if (Scalars.lessThan(dist, distThreshold) || init) { // if close enough to current waypoint switch to next
            i = (i + 1) % waypoints.length();
            goal = waypoints.get(i);
            // skip waypoint if covered by obstacle FIXME will not work if other constraints added
            if (!plannerConstraint.isSatisfied(null, Arrays.asList(new StateTime(goal, RealScalar.ZERO)), null)) {
              System.out.print("skipping\n");
              i = (i + 1) % waypoints.length();
              goal = waypoints.get(i);
            }
            head = trajEntity.getFutureTrajectoryUntil(trajEntity.delayHint());
            System.out.print("plan to goal \n");
            planToGoal(head, goal);
            init = false;
          } else {
            try {
              Thread.sleep(50);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
          }
        }
      }
    });
    thread.start();
  }

  /** starts planning towards a goal
   * 
   * @param TrajectoryPlannerCallback
   * @param head
   * @param goal */
  protected abstract void planToGoal(List<TrajectorySample> head, Tensor goal);
}
