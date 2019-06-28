// code by jph
package ch.ethz.idsc.owl.rrts.core;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public interface Transition {
  /** @return start state of this transition */
  Tensor start();

  /** @return end state of this transition */
  Tensor end();

  /** TODO if length() is part of transition interface the function requires a precise definition:
   * length() == Euclidean distance?
   * 
   * @return length of transition */
  Scalar length();

  /** @param minResolution is positive
   * @return */
  Tensor sampled(Scalar minResolution);

  /** @param steps > 0
   * @return */
  Tensor sampled(int steps);
}
