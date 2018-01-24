// code by jph
package ch.ethz.idsc.owl.gui.ani;

import java.util.List;
import java.util.Objects;

import ch.ethz.idsc.owl.data.Lists;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.math.state.EpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.TrajectoryControl;
import ch.ethz.idsc.owl.math.state.TrajectoryRegionQuery;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** entity executes flows along a given trajectory */
public abstract class TrajectoryEntity extends AbstractEntity {
  private final TrajectoryControl trajectoryControl;
  private List<TrajectorySample> trajectory = null;

  public TrajectoryEntity(EpisodeIntegrator episodeIntegrator, TrajectoryControl trajectoryControl) {
    super(episodeIntegrator, trajectoryControl);
    this.trajectoryControl = trajectoryControl;
  }

  public synchronized void setTrajectory(List<TrajectorySample> trajectory) {
    this.trajectory = trajectory;
    trajectoryControl.setTrajectory(trajectory);
  }

  // /** @param delay
  // * @return trajectory until delay[s] in the future of entity,
  // * or current position if entity does not have a trajectory */
  public final synchronized List<TrajectorySample> getFutureTrajectoryUntil(Scalar delay) {
    return trajectoryControl.getFutureTrajectoryUntil(getStateTimeNow(), delay);
  }

  /** @param delay
   * @return estimated location of agent after given delay */
  public final Tensor getEstimatedLocationAt(Scalar delay) {
    if (Objects.isNull(trajectory))
      return getStateTimeNow().state();
    List<TrajectorySample> relevant = trajectoryControl.getFutureTrajectoryUntil(getStateTimeNow(), delay);
    return Lists.getLast(relevant).stateTime().state();
  }

  public abstract PlannerType getPlannerType();

  // /** @return control vector to feed the episodeIntegrator in case no planned trajectory is available */
  // protected abstract Tensor fallbackControl();
  /** @return delay between now and the future point in time from when to divert to a new trajectory */
  public abstract Scalar delayHint();

  /** @param obstacleQuery
   * @param goal for instance {px, py, angle}
   * @return */
  public abstract TrajectoryPlanner createTrajectoryPlanner(TrajectoryRegionQuery obstacleQuery, Tensor goal);
}
