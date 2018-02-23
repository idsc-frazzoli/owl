// code by jph
package ch.ethz.idsc.owl.glc.adapter;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;

import ch.ethz.idsc.owl.data.LinearRasterMap;
import ch.ethz.idsc.owl.data.RasterMap;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.StateTimeRegionCallback;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** distinguishes the first 2 coordinates of StateTime::state */
public class SparseStateTimeRegionMembers implements StateTimeRegionCallback, Serializable {
  /** magic constants of scale are not universal but are suitable for most examples */
  private final RasterMap<StateTime> rasterMap = new LinearRasterMap<>(Tensors.vector(10, 10));

  @Override // from StateTimeRegionCallback
  public void notify_isMember(StateTime stateTime) {
    Tensor x = stateTime.state();
    rasterMap.put(1 == x.length() //
        ? x.copy().append(RealScalar.ZERO)
        : x.extract(0, 2), stateTime);
  }

  @Override // from StateTimeCollector
  public Collection<StateTime> getMembers() {
    return Collections.unmodifiableCollection(rasterMap.values());
  }
}
