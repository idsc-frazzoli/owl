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
import ch.ethz.idsc.tensor.Tensor;

/** maps time to control */
public class TrajectorySampleMap {
  private final NavigableMap<Scalar, TrajectorySample> navigableMap;

  public TrajectorySampleMap(List<TrajectorySample> trajectory) {
    navigableMap = trajectory.stream().collect(Collectors.toMap( //
        trajectorySample -> trajectorySample.stateTime().time(), //
        Function.identity(), //
        (u, v) -> null, //
        TreeMap::new));
  }

  public Optional<Tensor> getControl(Scalar now) {
    Entry<Scalar, TrajectorySample> entry = navigableMap.higherEntry(now);
    if (Objects.isNull(entry))
      return Optional.empty();
    return entry.getValue().getControl();
  }
}
