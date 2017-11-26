// code by jph
package ch.ethz.idsc.owl.rrts.core;

import ch.ethz.idsc.tensor.Scalar;

public interface TransitionCostFunction {
  /** @param transition
   * @return cost of given transition */
  Scalar cost(Transition transition);
}
