// code by jph
package ch.ethz.idsc.owl.glc.adapter;

import ch.ethz.idsc.tensor.Tensor;

@FunctionalInterface
public interface GoalConsumer {
  /** @param goal */
  void accept(Tensor goal);
}
