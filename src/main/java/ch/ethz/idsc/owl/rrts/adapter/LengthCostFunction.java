// code by jph
package ch.ethz.idsc.owl.rrts.adapter;

import ch.ethz.idsc.owl.rrts.core.RrtsNode;
import ch.ethz.idsc.owl.rrts.core.Transition;
import ch.ethz.idsc.owl.rrts.core.TransitionCostFunction;
import ch.ethz.idsc.tensor.Scalar;

/** TransitionCostFunction that is a function in Transition::length() */
public enum LengthCostFunction implements TransitionCostFunction {
  INSTANCE;

  @Override // from TransitionCostFunction
  public Scalar cost(RrtsNode rrtsNode, Transition transition) {
    return transition.length();
  }
}
