// code by jph
package ch.ethz.idsc.owl.rrts.core;

import ch.ethz.idsc.owl.rrts.adapter.LengthCostFunction;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public interface Transition {
  /** @return start state of this transition */
  Tensor start();

  /** @return end state of this transition */
  Tensor end();

  /** @return length of transition
   * @see TransitionCostFunction
   * @see LengthCostFunction */
  Scalar length();

  /** @param minResolution is positive
   * @return */
  TransitionSamplesWrap sampled(Scalar minResolution);

  /** @param steps > 0
   * @return */
  TransitionSamplesWrap sampled(int steps);
}
