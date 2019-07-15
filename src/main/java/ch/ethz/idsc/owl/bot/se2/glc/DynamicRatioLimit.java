// code by gjoel
package ch.ethz.idsc.owl.bot.se2.glc;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Clip;

// TODO JPH class is obsolete
public interface DynamicRatioLimit {
  /** @param state of car
   * @param speed of car
   * @return dependent limit on turning ratio */
  Clip at(Tensor state, Scalar speed);
}
