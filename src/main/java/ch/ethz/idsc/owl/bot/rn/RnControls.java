// code by jph
package ch.ethz.idsc.owl.bot.rn;

import java.util.Collection;

import ch.ethz.idsc.owl.math.DoubleIntegratorStateSpaceModel;
import ch.ethz.idsc.owl.math.SingleIntegratorStateSpaceModel;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.red.Norm;

/** utility functions for controls in R^n in combination with
 * {@link SingleIntegratorStateSpaceModel}
 * {@link DoubleIntegratorStateSpaceModel}
 * 
 * class is intentionally public */
/* package */ enum RnControls {
  ;
  /** @param controls
   * @return max of norm 2 of given controls in R^n */
  public static Scalar maxSpeed(Collection<Flow> controls) {
    return controls.stream() //
        .map(Flow::getU) //
        .map(Norm._2::ofVector) //
        .reduce(Max::of).get();
  }
}
