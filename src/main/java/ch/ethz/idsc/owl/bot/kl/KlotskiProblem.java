// code by jph
package ch.ethz.idsc.owl.bot.kl;

import ch.ethz.idsc.owl.glc.core.StateTimeRaster;
import ch.ethz.idsc.tensor.Tensor;

/* package */ interface KlotskiProblem {
  // TODO rename
  Tensor getState();

  Tensor size();

  StateTimeRaster stateTimeRaster();

  Tensor getGoal();

  Tensor getFrame();

  String name();
}
