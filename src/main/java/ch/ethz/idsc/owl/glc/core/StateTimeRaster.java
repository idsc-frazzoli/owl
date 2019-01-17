// code by jph
package ch.ethz.idsc.owl.glc.core;

import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.Tensor;

@FunctionalInterface
public interface StateTimeRaster {
  /** Example: Floor(eta .* represent(state))
   * 
   * @param stateTime
   * @return */
  Tensor convertToKey(StateTime stateTime);
}
