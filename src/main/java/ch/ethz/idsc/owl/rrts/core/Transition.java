// code by jph
package ch.ethz.idsc.owl.rrts.core;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public interface Transition {
  /** @return start state of this transition */
  Tensor start();

  /** @return end state of this transition */
  Tensor end();

  /** @return length of transition; length does not have to be Euclidean length but
   * is an abstract measure, which is a concept used in relation with minResolution */
  Scalar length();
  /** FUNCTIONALITY BELOW IS ONLY FOR COLLISION CHECKING AND RENDERING */
  // ---

  /** FIXME GJOEL/JPH doc/define if start or end are inclusive
   * JAN: "probably start should not be part of the returned samples"
   * 
   * @param minResolution is strictly positive
   * @return */
  Tensor sampled(Scalar minResolution);

  /** @param minResolution is positive
   * @return */
  TransitionWrap wrapped(Scalar minResolution);

  /** Hint: function is suitable to efficiently draw transition as path2d
   * 
   * @param minResolution
   * @param minSteps
   * @return sequence of points on transition that can be connected with straight lines */
  Tensor linearized(Scalar minResolution);
}
