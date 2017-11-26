// code by jph
package ch.ethz.idsc.owl.bot.delta;

import java.util.Collection;

import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.red.Norm;

public enum DeltaControls {
  ;
  /** @param controls
   * @return */
  public static Scalar maxSpeed(Collection<Flow> controls) {
    return controls.stream().map(Flow::getU).map(Norm._2::ofVector).reduce(Max::of).get();
  }
}
