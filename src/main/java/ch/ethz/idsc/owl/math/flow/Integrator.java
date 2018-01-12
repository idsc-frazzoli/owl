// code by jph
package ch.ethz.idsc.owl.math.flow;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** integrator of time-invariant differential constraint */
public interface Integrator {
  /** @param flow
   * @param x
   * @param h
   * @return moves x along flow for time duration h, i.e. "x + flow(x) * h" */
  Tensor step(Flow flow, Tensor x, Scalar h);
}
