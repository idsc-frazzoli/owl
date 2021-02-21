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

  /***************************************************/
  /** FUNCTIONALITY BELOW IS ONLY FOR
   * COLLISION CHECKING AND RENDERING */
  /***************************************************/
  /** sequence of samples along transition that are not further than given
   * minResolution apart.
   * 
   * @param minResolution strictly positive
   * @return sequence of points (start, ..., end] */
  Tensor sampled(Scalar minResolution);

  /** create {@link TransitionWrap} containing samples from {@link #sampled(Scalar)} and spacing
   * i.e. distance between current and previous sample (resp. {@link #start()} for the first sample),
   * hence samples and spacing have the same length
   * 
   * @param minResolution strictly positive
   * @return */
  TransitionWrap wrapped(Scalar minResolution);

  /** sequence of samples along transition that are not further than given
   * minResolution apart, except if they can be joined with a straight line.
   * 
   * Hint: function is suitable to efficiently draw transition as path2d
   * 
   * @param minResolution strictly positive
   * @return sequence of points [start, ..., end] on transition that can be connected with straight lines */
  Tensor linearized(Scalar minResolution);
}
