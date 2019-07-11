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
    return new AbstractTransition(start, end, transitionSpace.distance(end, start)) {
      final Transition transition = transitionSpace.connect(end, start);

      @Override // from Transition
      public Tensor sampled(Scalar minResolution) {
        return swap(transition.sampled(minResolution));
      }

      @Override // from Transition
      public Tensor sampled(int steps) {
        return swap(transition.sampled(steps));
      }

      private Tensor swap(Tensor samples) {
        return Reverse.of(samples.extract(1, samples.length()).append(start));
      }

      @Override // from Transition
      public TransitionWrap wrapped(int steps) {
        if (steps < 1)
          throw TensorRuntimeException.of(length(), RealScalar.of(steps));
        Tensor samples = sampled(steps);
        Tensor spacing = Array.zeros(samples.length());
        IntStream.range(0, samples.length()).forEach(i -> spacing.set(i > 0 //
            ? distance(samples.get(i - 1), samples.get(i)) //
            : samples.Get(i, 0).zero(), i));
        return new TransitionWrap(samples, spacing);
      }
    };
  }

  @Override // from TransitionSpace
  public Scalar distance(Tensor start, Tensor end) {
    return transitionSpace.distance(end, start);
  }
}
