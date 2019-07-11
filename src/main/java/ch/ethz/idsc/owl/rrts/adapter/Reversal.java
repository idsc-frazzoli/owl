// code by gjoel
package ch.ethz.idsc.owl.rrts.adapter;

import java.io.Serializable;
import java.util.stream.IntStream;

import ch.ethz.idsc.owl.rrts.core.Transition;
import ch.ethz.idsc.owl.rrts.core.TransitionSpace;
import ch.ethz.idsc.owl.rrts.core.TransitionWrap;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.PadLeft;
import ch.ethz.idsc.tensor.alg.Reverse;

public class Reversal implements TransitionSpace, Serializable {
  public static TransitionSpace of(TransitionSpace transitionSpace) {
    return new Reversal(transitionSpace);
  }

  // ---
  private final TransitionSpace transitionSpace;

  private Reversal(TransitionSpace transitionSpace) {
    this.transitionSpace = transitionSpace;
  }

  @Override // from TransitionSpace
  public Transition connect(Tensor start, Tensor end) {
    Transition _transition = transitionSpace.connect(end, start);
    return new AbstractTransition(start, end, _transition.length()) {
      final Transition transition = _transition;

      @Override // from Transition
      public Tensor sampled(Scalar minResolution) {
        return swap(transition.sampled(minResolution));
      }

      @Override // from Transition
      public Tensor sampled(int steps) {
        return swap(transition.sampled(steps));
      }

      private Tensor swap(Tensor samples) {
        // TODO not efficient
        return PadLeft.with(start, samples.length()).apply(Reverse.of(samples.extract(1, samples.length())));
      }

      @Override // from Transition
      public TransitionWrap wrapped(int steps) {
        if (steps < 1)
          throw TensorRuntimeException.of(length(), RealScalar.of(steps));
        Tensor samples = sampled(steps);
        Tensor spacing = Array.zeros(samples.length());
        IntStream.range(0, samples.length()).forEach(i -> spacing.set(i > 0 //
            ? connect(samples.get(i - 1), samples.get(i)).length() //
            : samples.Get(i, 0).zero(), i));
        return new TransitionWrap(samples, spacing);
      }
    };
  }
}
