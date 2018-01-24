// code by jph
package ch.ethz.idsc.owl.math.state;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

// FIXME
public abstract class TemporalTrajectoryControl extends AbstractTrajectoryControl {
  private List<TrajectorySample> trajectory = null;
  private TrajectorySampleMap trajectorySampleMap;

  public TemporalTrajectoryControl(Tensor fallback_control) {
    super(fallback_control);
  }

  @Override
  public final synchronized void setTrajectory(List<TrajectorySample> trajectory) {
    this.trajectory = trajectory;
    trajectorySampleMap = new TrajectorySampleMap(trajectory);
  }

  @Override
  protected final synchronized Optional<Tensor> protected_control(StateTime tail, Scalar now) {
    if (Objects.nonNull(trajectory)) {
      Optional<Flow> optional = trajectorySampleMap.getFlowAt(now);
      if (optional.isPresent())
        return Optional.of(optional.get().getU());
    }
    return Optional.empty();
  }
}
