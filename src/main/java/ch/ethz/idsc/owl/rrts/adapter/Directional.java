// code by gjoel
package ch.ethz.idsc.owl.rrts.adapter;

import java.io.Serializable;

import ch.ethz.idsc.owl.rrts.core.Transition;
import ch.ethz.idsc.owl.rrts.core.TransitionSpace;
import ch.ethz.idsc.owl.rrts.core.TransitionWrap;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.ArgMin;
import ch.ethz.idsc.tensor.red.Min;

public class Directional implements TransitionSpace, Serializable {
  public static TransitionSpace of(TransitionSpace transitionSpace) {
    return new Directional(transitionSpace);
  }
  // ---
  private final TransitionSpace forwardTransitionSpace;
  private final TransitionSpace backwardTransitionSpace;

  private Directional(TransitionSpace transitionSpace) {
    forwardTransitionSpace = transitionSpace;
    backwardTransitionSpace = Reversal.of(transitionSpace);
  }

  @Override // from TransitionSpace
  public Transition connect(Tensor start, Tensor end) {
    Tensor lengths = Tensors.of( //
        forwardTransitionSpace.distance(start, end), //
        backwardTransitionSpace.distance(start, end));
    int index = ArgMin.of(lengths);
    return new AbstractTransition(start, end, lengths.Get(index)) {
      final Transition transition = (index == 0 //
          ? forwardTransitionSpace //
          : backwardTransitionSpace).connect(start, end);

      @Override // from Transition
      public Tensor sampled(Scalar minResolution) {
        return transition.sampled(minResolution);
      }

      @Override // from Transition
      public Tensor sampled(int steps) {
        return transition.sampled(steps);
      }

      @Override // from Transition
      public TransitionWrap wrapped(int steps) {
        return transition.wrapped(steps);
      }
    };
  }

  @Override // from TransitionSpace
  public Scalar distance(Tensor start, Tensor end) {
    return Min.of( //
        forwardTransitionSpace.distance(start, end), //
        backwardTransitionSpace.distance(start, end));
  }
}
