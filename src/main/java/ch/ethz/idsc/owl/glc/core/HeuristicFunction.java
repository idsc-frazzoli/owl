// code by bapaden and jph
package ch.ethz.idsc.owl.glc.core;

import java.io.Serializable;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public interface HeuristicFunction extends Serializable {
  /** if a lower bound of the cost to goal is unknown,
   * the function should return {@link RealScalar#ZERO}.
   * 
   * <p>it is imperative that the function does not return a greater number than
   * is absolutely necessary to reach the goal.
   * 
   * <p>if instance encodes a non-trivial heuristic, i.e. a return value not
   * always equals to zero, the function should throw an exception if x == null
   * 
   * @param x encodes state from which min cost to goal is evaluated
   * @return lower bound of cost to goal */
  Scalar minCostToGoal(Tensor x);
}
