// code by jph
package ch.ethz.idsc.owl.math.state;

import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.Interpolation;
import ch.ethz.idsc.tensor.opt.LinearInterpolation;
import ch.ethz.idsc.tensor.sca.Clip;

/** wrapper around trajectory for fast search and control query */
public class TrajectoryWrap {
  public static TrajectoryWrap of(List<TrajectorySample> trajectory) {
    return new TrajectoryWrap(trajectory);
  }
  // ---

  private final List<TrajectorySample> trajectory;
  private final NavigableMap<Scalar, TrajectorySample> navigableMap;

  /** @param trajectory non-empty */
  private TrajectoryWrap(List<TrajectorySample> trajectory) {
    this.trajectory = Collections.unmodifiableList(trajectory);
    navigableMap = trajectory.stream().collect(Collectors.toMap( //
        trajectorySample -> trajectorySample.stateTime().time(), //
        Function.identity(), (u, v) -> null, TreeMap::new));
  }

  public List<TrajectorySample> trajectory() {
    return trajectory;
  }

  /** @param now
   * @return control to reach trajectory sample registered at time strictly greater than given now */
  public Optional<TrajectorySample> findTrajectorySample(Scalar now) {
    Entry<Scalar, TrajectorySample> entry = navigableMap.higherEntry(now);
    return Optional.ofNullable(entry == null ? null : entry.getValue());
  }

  // TODO define corner cases, document function and put to use
  /** @param now
   * @return empty if now is outside of time defined by trajectory */
  public Optional<TrajectorySample> interpolationAt(Scalar now) {
    // FIXME implementation throws exception in many cases
    Entry<Scalar, TrajectorySample> lo = navigableMap.floorEntry(now);
    Entry<Scalar, TrajectorySample> hi = navigableMap.higherEntry(now);
    Clip clip = Clip.function(lo.getKey(), hi.getKey());
    Scalar index = clip.rescale(now);
    Interpolation interpolation = LinearInterpolation.of(Tensors.of( //
        lo.getValue().stateTime().state(), //
        hi.getValue().stateTime().state()));
    return Optional.of(new TrajectorySample( //
        new StateTime(interpolation.at(index), now), //
        hi.getValue().getFlow().orElse(null)));
  }

  /** @param now
   * @return control to reach trajectory sample registered at time strictly greater than given now */
  public Optional<Tensor> findControl(Scalar now) {
    // Optional<TrajectorySample> optional = findTrajectorySample(now);
    // return optional.isPresent() ? optional.get().getControl() : Optional.empty();
    Entry<Scalar, TrajectorySample> entry = navigableMap.higherEntry(now);
    return Objects.isNull(entry) ? Optional.empty() : entry.getValue().getControl();
  }

  /** @param now
   * @return true, if given now is strictly less than time of last trajectory sample */
  public boolean hasRemaining(Scalar now) {
    return Scalars.lessThan(now, navigableMap.lastKey());
  }
}
