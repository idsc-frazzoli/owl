// code by jph
package ch.ethz.idsc.owl.rrts.core;

import java.util.List;

import ch.ethz.idsc.owl.math.state.StateTime;
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

  /** @param t0 time at start()
   * @param ofs is non-negative and strictly less than dt
   * @param dt
   * @return */
  // TODO API not finalize: is List<Tensor> sufficient?
  List<StateTime> sampled(Scalar t0, Scalar ofs, Scalar dt);

  StateTime splitAt(Scalar t1);
}
