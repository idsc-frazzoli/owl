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

  /** @return time to traverse this transition
   * @see TransitionCostFunction
   * @see LengthCostFunction */
  Scalar length();

  /** @param ofs is non-negative and strictly less than dt
   * @param dt
   * @return */
  Tensor sampled(Scalar ofs, Scalar dt);

  /** @param scalar in the interval [0, length()]
   * @return point on transition at given parameter value */
  Tensor splitAt(Scalar scalar);
}
