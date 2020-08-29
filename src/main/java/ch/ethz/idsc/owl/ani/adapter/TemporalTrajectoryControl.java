// code by jph
package ch.ethz.idsc.owl.ani.adapter;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import ch.ethz.idsc.owl.ani.api.ProviderRank;
import ch.ethz.idsc.owl.ani.api.TrajectoryControl;
import ch.ethz.idsc.owl.glc.adapter.Trajectories;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.owl.math.state.TrajectoryWrap;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** generic controller to execute time dependent trajectories */
public class TemporalTrajectoryControl implements TrajectoryControl, Serializable {
  /** @return */
  public static TrajectoryControl createInstance() {
    return new TemporalTrajectoryControl();
  }

  /***************************************************/
  private TrajectoryWrap trajectoryWrap = null;

  private TemporalTrajectoryControl() {
  }

  @Override
  public synchronized void trajectory(List<TrajectorySample> trajectory) {
    trajectoryWrap = Objects.isNull(trajectory) //
        ? null
        : TrajectoryWrap.of(trajectory);
  }

  @Override
  public synchronized Optional<Tensor> control(StateTime tail, Scalar now) {
    if (Objects.nonNull(trajectoryWrap))
      if (trajectoryWrap.isRelevant(now)) { // control values now or upcoming
        if (trajectoryWrap.isDefined(now)) // control values now
          return Optional.of(trajectoryWrap.getControl(now));
      } else // control values are in the past
        trajectory(null); // trajectory is not relevant anymore
    return Optional.empty();
  }

  @Override
  public List<TrajectorySample> getFutureTrajectoryUntil(StateTime tail, Scalar delay) {
    Scalar tail_delayed = tail.time().add(delay);
    if (Objects.isNull(trajectoryWrap)) {
      StateTime stateTime = new StateTime(tail.state(), tail_delayed);
      return Collections.singletonList(TrajectorySample.head(stateTime));
    }
    return trajectoryWrap.trajectory().stream() //
        .filter(Trajectories.untilTime(tail_delayed)) //
        .collect(Collectors.toList());
  }

  @Override
  public ProviderRank getProviderRank() {
    return ProviderRank.AUTONOMOUS;
  }
}
