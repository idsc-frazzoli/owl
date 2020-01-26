// code by jph
package ch.ethz.idsc.owl.bot.kl;

import ch.ethz.idsc.tensor.Tensor;

/* package */ interface KlotskiProblem {
  Tensor getBoard();

  Tensor getGoal();

  String name();
}
