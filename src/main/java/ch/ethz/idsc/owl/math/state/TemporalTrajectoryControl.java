// code by jph
package ch.ethz.idsc.owl.math.state;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public class TemporalTrajectoryControl extends AbstractTrajectoryControl {
  private List<TrajectorySample> trajectory = null;
  private TrajectorySampleMap trajectorySampleMap;

  public TemporalTrajectoryControl(Tensor fallback) {
    super(fallback);
  }

  @Override
  public final synchronized void setTrajectory(List<TrajectorySample> trajectory) {
    this.trajectory = trajectory;
    trajectorySampleMap = new TrajectorySampleMap(trajectory);
  }

  @Override
  protected final synchronized Optional<Tensor> protected_control(StateTime tail, Scalar now) {
    if (Objects.nonNull(trajectory))
      return trajectorySampleMap.getControl(now);
    return Optional.empty();
  }

  @Override
  public List<TrajectorySample> getFutureTrajectoryUntil(StateTime tail, Scalar delay) {
    // TODO
    return Collections.singletonList(TrajectorySample.head(tail));
  }
}
