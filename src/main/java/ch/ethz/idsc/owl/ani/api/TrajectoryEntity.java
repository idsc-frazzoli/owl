// code by jph
package ch.ethz.idsc.owl.ani.api;

import java.util.List;
import java.util.Objects;

import ch.ethz.idsc.owl.data.Lists;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.math.state.EpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.owl.math.state.TrajectoryWrap;
import ch.ethz.idsc.sophus.math.TensorMetric;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** entity executes flows along a given trajectory */
public abstract class TrajectoryEntity extends AbstractEntity implements TrajectoryListener, TensorMetric {
  private final TrajectoryControl trajectoryControl;
  protected TrajectoryWrap trajectoryWrap = null;

  public TrajectoryEntity(EpisodeIntegrator episodeIntegrator, TrajectoryControl trajectoryControl) {
    super(episodeIntegrator);
    add(trajectoryControl);
    this.trajectoryControl = trajectoryControl;
  }

  @Override
  public synchronized void trajectory(List<TrajectorySample> trajectory) {
    trajectoryWrap = TrajectoryWrap.of(trajectory);
    trajectoryControl.trajectory(trajectory);
  }

  /** @param delay
   * @return trajectory until delay[s] in the future of entity,
   * or current position if entity does not have a trajectory */
  public final synchronized List<TrajectorySample> getFutureTrajectoryUntil(Scalar delay) {
    return trajectoryControl.getFutureTrajectoryUntil(getStateTimeNow(), delay);
  }

  /** @param delay
   * @return estimated location of agent after given delay */
  public final Tensor getEstimatedLocationAt(Scalar delay) {
    if (Objects.isNull(trajectoryWrap))
      return getStateTimeNow().state();
    List<TrajectorySample> relevant = trajectoryControl.getFutureTrajectoryUntil(getStateTimeNow(), delay);
    return Lists.getLast(relevant).stateTime().state();
  }

  /** @return delay between now and the future point in time from when to divert to a new trajectory */
  public abstract Scalar delayHint();

  /** @param plannerConstraint
   * @param goal for instance {px, py, angle}
   * @return */
  public abstract TrajectoryPlanner createTrajectoryPlanner(PlannerConstraint plannerConstraint, Tensor goal);
}
