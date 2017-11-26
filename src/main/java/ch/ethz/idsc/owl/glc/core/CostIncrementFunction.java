// code by jph
package ch.ethz.idsc.owl.glc.core;

import java.util.List;

import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.Scalar;

/** used in combination with a {@link HeuristicFunction}.
 * 
 * standalone implementations include {@link ImageCostFunction} */
public interface CostIncrementFunction {
  /** @param glcNode from which trajectory starts
   * @param trajectory
   * @param flow along which trajectory was computed
   * @return cost of trajectory along flow */
  Scalar costIncrement(GlcNode glcNode, List<StateTime> trajectory, Flow flow);
}
