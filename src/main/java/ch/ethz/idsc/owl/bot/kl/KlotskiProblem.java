// code by jph
package ch.ethz.idsc.owl.bot.kl;

import ch.ethz.idsc.owl.glc.core.StateTimeRaster;
import ch.ethz.idsc.tensor.Tensor;

/* package */ interface KlotskiProblem {
  /** @return list of stones with type and initial position */
  Tensor startState();

  /** @return vector of length 2 */
  Tensor size();

  /** @return */
  StateTimeRaster stateTimeRaster();

  /** @return vector of length 3 */
  Tensor getGoal();

  Tensor getFrame();

  String name();
}
