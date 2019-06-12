// code by gjoel
package ch.ethz.idsc.owl.bot.se2.rrts;

import ch.ethz.idsc.owl.rrts.adapter.AbstractTransition;
import ch.ethz.idsc.tensor.Tensor;

/* package */ abstract class Se2Transition extends AbstractTransition {
  public Se2Transition(Tensor start, Tensor end) {
    super(start, end);
  }
}
