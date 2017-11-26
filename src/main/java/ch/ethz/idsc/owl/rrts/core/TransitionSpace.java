// code by jph
package ch.ethz.idsc.owl.rrts.core;

import ch.ethz.idsc.tensor.Tensor;

public interface TransitionSpace {
  /** @param start state
   * @param end state
   * @return transition that represents the (unique) connection between the start and end state */
  Transition connect(Tensor start, Tensor end);
}
