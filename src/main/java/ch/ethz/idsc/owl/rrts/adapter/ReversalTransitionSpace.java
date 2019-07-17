// code by gjoel
package ch.ethz.idsc.owl.rrts.adapter;

import java.io.Serializable;
import java.util.stream.IntStream;

import ch.ethz.idsc.owl.rrts.core.Transition;
import ch.ethz.idsc.owl.rrts.core.TransitionSpace;
import ch.ethz.idsc.owl.rrts.core.TransitionWrap;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.alg.Array;

public class ReversalTransitionSpace implements TransitionSpace, Serializable {
  public static TransitionSpace of(TransitionSpace transitionSpace) {
    return new ReversalTransitionSpace(transitionSpace);
  }

  // ---
  private final TransitionSpace transitionSpace;

  private ReversalTransitionSpace(TransitionSpace transitionSpace) {
    this.transitionSpace = transitionSpace;
  }

  @Override // from TransitionSpace
  public DirectedTransition connect(Tensor start, Tensor end) {
    Transition transition = transitionSpace.connect(end, start);
    return new ReversalTransition(transition) {
      @Override // from Transition
      public TransitionWrap wrapped(int steps) {
        if (steps < 1)
          throw TensorRuntimeException.of(length(), RealScalar.of(steps));
        Tensor samples = sampled(steps);
        Tensor spacing = Array.zeros(samples.length());
        // TODO GJOEL use of function connect is generally not correct
        IntStream.range(0, samples.length()).forEach(i -> spacing.set(i > 0 //
            ? connect(samples.get(i - 1), samples.get(i)).length() //
            : samples.Get(i, 0).zero(), i));
        return new TransitionWrap(samples, spacing);
      }
    };
  }
}
