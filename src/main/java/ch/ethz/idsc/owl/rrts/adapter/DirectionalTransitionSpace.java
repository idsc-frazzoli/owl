// code by gjoel
package ch.ethz.idsc.owl.rrts.adapter;

import java.io.Serializable;

import ch.ethz.idsc.owl.rrts.core.Transition;
import ch.ethz.idsc.owl.rrts.core.TransitionSpace;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.ArgMin;

public class DirectionalTransitionSpace implements TransitionSpace, Serializable {
  public static TransitionSpace of(TransitionSpace transitionSpace) {
    return new DirectionalTransitionSpace(transitionSpace);
  }

  // ---
  private final TransitionSpace forwardTransitionSpace;
  private final TransitionSpace backwardTransitionSpace;

  private DirectionalTransitionSpace(TransitionSpace transitionSpace) {
    forwardTransitionSpace = transitionSpace;
    backwardTransitionSpace = ReversalTransitionSpace.of(transitionSpace);
  }

  @Override // from TransitionSpace
  public DirectedTransition connect(Tensor start, Tensor end) {
    Tensor lengths = Tensors.of( //
        forwardTransitionSpace.connect(start, end).length(), //
        backwardTransitionSpace.connect(start, end).length());
    int index = ArgMin.of(lengths);
    final Transition transition = (index == 0 //
        ? forwardTransitionSpace //
        : backwardTransitionSpace).connect(start, end);
    return new DirectedTransition(transition, lengths.Get(index), index == 0);
  }
}
