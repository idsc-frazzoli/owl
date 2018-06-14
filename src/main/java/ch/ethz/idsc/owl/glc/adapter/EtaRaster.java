// code by jph
package ch.ethz.idsc.owl.glc.adapter;

import java.io.Serializable;

import ch.ethz.idsc.owl.glc.core.StateTimeRaster;
import ch.ethz.idsc.owl.math.StateTimeTensorFunction;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Floor;

/** mapping from state time to domain coordinates
 * 
 * <p>The default value drops time information and only considers
 * {@link StateTime#state()}.
 * 
 * Examples: identity, mod, log, ... */
/** Floor(eta .* represent(state))
 * 
 * @param stateTime
 * @return */
public class EtaRaster implements StateTimeRaster, Serializable {
  public static StateTimeRaster state(Tensor eta) {
    return new EtaRaster(eta, StateTime::state);
  }

  public static StateTimeRaster joined(Tensor eta) {
    return new EtaRaster(eta, StateTime::joined);
  }

  // ---
  private final Tensor eta;
  private final StateTimeTensorFunction represent;

  public EtaRaster(Tensor eta, StateTimeTensorFunction represent) {
    this.eta = eta.copy();
    this.represent = represent;
  }

  @Override // from StateTimeRasterization
  public Tensor convertToKey(StateTime stateTime) {
    return eta.pmul(represent.apply(stateTime)).map(Floor.FUNCTION);
  }

  public Tensor eta() {
    return eta.unmodifiable();
  }
}
