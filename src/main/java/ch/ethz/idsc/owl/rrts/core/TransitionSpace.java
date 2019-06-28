// code by jph
package ch.ethz.idsc.owl.rrts.core;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** factory for {@link Transition}s */
// TODO functional interface
public interface TransitionSpace {
  /** @param start state
   * @param end state
   * @return transition that represents the (unique) connection between the start and end state */
  Transition connect(Tensor start, Tensor end);

  /** @param start state
   * @param end state
   * @return Scalar distance between start and end */
  // TODO function deprecated
  Scalar distance(Tensor start, Tensor end);

  /** @param transition
   * @return Scalar distance of Transition */
  // TODO function deprecated
  Scalar distance(Transition transition);
}
