// code by jph
package ch.ethz.idsc.owl.glc.adapter;

import java.io.Serializable;

import ch.ethz.idsc.owl.bot.lv.LvStateSpaceModel;
import ch.ethz.idsc.owl.glc.core.StateTimeRaster;
import ch.ethz.idsc.owl.math.StateTimeTensorFunction;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Floor;

/** mapping from state time to domain coordinates according to the standard projection
 * Floor(eta .* represent(state))
 * 
 * <p>Examples for represent are:
 * identity for R^n
 * mod for So2
 * log for {@link LvStateSpaceModel}
 * 
 * <p>The default value drops time information and only considers
 * {@link StateTime#state()}. */
public class EtaRaster implements StateTimeRaster, Serializable {
  /** @param eta
   * @return uniform raster for {@link StateTime#state()} */
  public static StateTimeRaster state(Tensor eta) {
    return new EtaRaster(eta, StateTime::state);
  }

  /** @param eta
   * @return uniform raster for {@link StateTime#joined()} */
  public static StateTimeRaster joined(Tensor eta) {
    return new EtaRaster(eta, StateTime::joined);
  }

  /** @param eta
   * @param dt in exact precision
   * @param represent for instance StateTime::joined
   * @return
   * @throws Exception if dt is not in exact precision */
  public static StateTimeRaster timeDependent(Tensor eta, Scalar dt, StateTimeTensorFunction represent) {
    return new EtaRaster(eta.copy().append(ExactScalarQ.require(dt).reciprocal()), represent);
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

  /** @return unmodifiable */
  public Tensor eta() {
    return eta.unmodifiable();
  }
}
