// code by jph
package ch.ethz.idsc.owl.math.state;

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

/** maps time to control */
public class TrajectorySampleMap {
  public static TrajectorySampleMap create(List<TrajectorySample> trajectory) {
    return new TrajectorySampleMap(trajectory);
  }
  // ---

  // check if only control are needed, in that case map can only store Optional<Tensor>
  private final NavigableMap<Scalar, TrajectorySample> navigableMap;

  /** @param trajectory non-empty */
  private TrajectorySampleMap(List<TrajectorySample> trajectory) {
    navigableMap = trajectory.stream().collect(Collectors.toMap( //
        trajectorySample -> trajectorySample.stateTime().time(), //
        Function.identity(), (u, v) -> null, TreeMap::new));
  }

  /** @param now
   * @return control to reach trajectory sample registered at time strictly greater than given now */
  public Optional<Tensor> getControl(Scalar now) {
    Entry<Scalar, TrajectorySample> entry = navigableMap.higherEntry(now);
    return Objects.isNull(entry) //
        ? Optional.empty()
        : entry.getValue().getControl();
  }

  /** @param now
   * @return true, if given now is strictly less than time of last trajectory sample */
  public boolean isValid(Scalar now) {
    return Scalars.lessThan(now, navigableMap.lastKey());
  }
}
