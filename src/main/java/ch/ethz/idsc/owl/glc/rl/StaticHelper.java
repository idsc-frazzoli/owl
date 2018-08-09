// code by jph
package ch.ethz.idsc.owl.glc.rl;

import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.HeuristicFunction;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.Scalar;

/* package */ enum StaticHelper {
  ;
  /** @param stateTime
   * @param heuristicFunction
   * @return */
  public static GlcNode createRoot(StateTime stateTime, HeuristicFunction heuristicFunction) {
    Scalar minCost = heuristicFunction.minCostToGoal(stateTime.state());
    return GlcNode.of(null, stateTime, minCost.zero(), minCost);
  }
}
