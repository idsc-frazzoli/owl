// code by jph
package ch.ethz.idsc.owl.rrts.core;

import ch.ethz.idsc.tensor.Scalar;

@FunctionalInterface
public interface TransitionCostFunction {
  /** @param rrtsNode at which transition starts
   * @param transition
   * @return cost of given transition */
  Scalar cost(RrtsNode rrtsNode, Transition transition);

  /** @return on how many prior transitions does this depend */
  default int influence() {
    return 0;
  }
}
