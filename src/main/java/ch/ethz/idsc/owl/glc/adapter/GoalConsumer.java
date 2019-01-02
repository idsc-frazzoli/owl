// code by jph
package ch.ethz.idsc.owl.glc.adapter;

import ch.ethz.idsc.tensor.Tensor;

public interface GoalConsumer {
  /** @param goal */
  void accept(Tensor goal);
}
