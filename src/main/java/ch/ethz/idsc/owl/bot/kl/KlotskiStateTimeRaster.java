// code by jph
package ch.ethz.idsc.owl.bot.kl;

import ch.ethz.idsc.owl.glc.core.StateTimeRaster;
import ch.ethz.idsc.owl.math.order.VectorLexicographic;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.Tensor;

/* package */ enum KlotskiStateTimeRaster implements StateTimeRaster {
  INSTANCE;

  @Override // from StateTimeRaster
  public Tensor convertToKey(StateTime stateTime) {
    return Tensor.of(stateTime.state().get(0).stream().sorted(VectorLexicographic.COMPARATOR));
  }
}
