// code by gjoel
package ch.ethz.idsc.owl.rrts.adapter;

import java.io.Serializable;

import ch.ethz.idsc.owl.rrts.core.Transition;
import ch.ethz.idsc.owl.rrts.core.TransitionSpace;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;

public class DirectionalTransitionSpace implements TransitionSpace, Serializable {
  /** @param transitionSpace
   * @return */
  public static TransitionSpace of(TransitionSpace transitionSpace) {
    return new DirectionalTransitionSpace(transitionSpace);
  }

  /***************************************************/
  private final TransitionSpace forwardTransitionSpace;
  private final TransitionSpace backwardTransitionSpace;

  private DirectionalTransitionSpace(TransitionSpace transitionSpace) {
    forwardTransitionSpace = transitionSpace;
    backwardTransitionSpace = ReversalTransitionSpace.of(transitionSpace);
  }

  @Override // from TransitionSpace
  public DirectedTransition connect(Tensor start, Tensor end) {
    Transition tf = forwardTransitionSpace.connect(start, end);
    Transition tr = backwardTransitionSpace.connect(start, end); // ReversalTransition
    boolean isForward = Scalars.lessEquals(tf.length(), tr.length());
    return isForward ? new DirectedTransition(tf, true) : (DirectedTransition) tr;
  }
}
