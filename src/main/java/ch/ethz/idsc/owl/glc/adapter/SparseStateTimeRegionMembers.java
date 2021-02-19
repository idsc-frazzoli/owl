// code by jph
package ch.ethz.idsc.owl.glc.adapter;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;

import ch.ethz.idsc.owl.data.LinearRasterMap;
import ch.ethz.idsc.owl.data.RasterMap;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.StateTimeRegionCallback;
import ch.ethz.idsc.sophus.math.d2.Extract2D;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Append;

/** distinguishes the first 2 coordinates of StateTime::state */
class SparseStateTimeRegionMembers implements StateTimeRegionCallback, Serializable {
  /** magic constants of scale are not universal but are suitable for most examples */
  private final RasterMap<StateTime> rasterMap = new LinearRasterMap<>(Tensors.vector(10, 10));

  @Override // from StateTimeRegionCallback
  public void notify_isMember(StateTime stateTime) {
    Tensor x = stateTime.state();
    Tensor key = 1 == x.length() //
        ? Append.of(x, RealScalar.ZERO)
        : Extract2D.FUNCTION.apply(x);
    rasterMap.put(key, stateTime);
  }

  @Override // from StateTimeCollector
  public Collection<StateTime> getMembers() {
    return Collections.unmodifiableCollection(rasterMap.values());
  }
}
