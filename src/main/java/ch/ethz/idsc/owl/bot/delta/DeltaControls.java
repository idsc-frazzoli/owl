// code by jph
package ch.ethz.idsc.owl.bot.delta;

import java.util.Collection;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.nrm.Vector2Norm;
import ch.ethz.idsc.tensor.red.Max;

/* package */ enum DeltaControls {
  ;
  /** @param controls
   * @return */
  public static Scalar maxSpeed(Collection<Tensor> controls) {
    return controls.stream().map(Vector2Norm::of).reduce(Max::of).get();
  }
}
