// code by jph
package ch.ethz.idsc.owl.math.state;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import ch.ethz.idsc.owl.glc.adapter.Trajectories;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** generic controller to execute time dependent trajectories */
public enum TemporalTrajectoryControl implements TrajectoryControl {
  INSTANCE;
  // ---
  private TrajectoryWrap trajectoryWrap = null;

  @Override
  public synchronized void setTrajectory(List<TrajectorySample> trajectory) {
    trajectoryWrap = Objects.isNull(trajectory) ? null : TrajectoryWrap.of(trajectory);
  }

  @Override
  public synchronized Optional<Tensor> control(StateTime tail, Scalar now) {
    if (Objects.nonNull(trajectoryWrap)) {
      if (trajectoryWrap.hasRemaining(now))
        return trajectoryWrap.findControl(now);
      System.err.println("out of trajectory");
      setTrajectory(null);
    }
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
