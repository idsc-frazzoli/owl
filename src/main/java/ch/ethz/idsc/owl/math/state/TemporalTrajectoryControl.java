// code by jph
package ch.ethz.idsc.owl.math.state;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;

public enum TemporalTrajectoryControl implements TrajectoryControl {
  INSTANCE;
  // ---
  private List<TrajectorySample> trajectory = null;
  private TrajectorySampleMap trajectorySampleMap;

  @Override
  public synchronized void setTrajectory(List<TrajectorySample> trajectory) {
    this.trajectory = trajectory;
    trajectorySampleMap = Objects.isNull(trajectory) ? null : TrajectorySampleMap.create(trajectory);
  }

  @Override
  public synchronized Optional<Tensor> control(StateTime tail, Scalar now) {
    if (Objects.nonNull(trajectory)) {
      if (trajectorySampleMap.isValid(now))
        return trajectorySampleMap.getControl(now);
      System.err.println("out of trajectory");
      setTrajectory(null);
    }
    return Optional.empty();
  }

  @Override
  public List<TrajectorySample> getFutureTrajectoryUntil(StateTime tail, Scalar delay) {
    Scalar tail_delay = tail.time().add(delay);
    if (Objects.isNull(trajectory))
      return Collections.singletonList(TrajectorySample.head(new StateTime(tail.state(), tail_delay)));
    return trajectory.stream() //
        .filter(trajectorySample -> Scalars.lessEquals(trajectorySample.stateTime().time(), tail_delay)) //
        .collect(Collectors.toList());
  }
}
