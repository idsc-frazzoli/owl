// code by jph
package ch.ethz.idsc.owl.glc.core;

import java.util.List;

import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** used in combination with a {@link HeuristicFunction}.
 * 
 * candidate implementations include ImageCostFunction */
@FunctionalInterface
public interface CostIncrementFunction {
  /** @param glcNode from which trajectory starts
   * @param trajectory
   * @param flow along which trajectory was computed
   * @return cost of trajectory along flow */
  Scalar costIncrement(GlcNode glcNode, List<StateTime> trajectory, Tensor flow);
}
