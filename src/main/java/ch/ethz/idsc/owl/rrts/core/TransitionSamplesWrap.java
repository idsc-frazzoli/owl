// code by gjoel
package ch.ethz.idsc.owl.rrts.core;

import java.util.stream.IntStream;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;

public class TransitionSamplesWrap {
  public static TransitionSamplesWrap of(Tensor samples, TransitionSpace transitionSpace) {
    Tensor spacing = Array.zeros(samples.length());
    IntStream.range(0, samples.length()).parallel().forEach(i -> spacing.set(i > 0 //
        ? transitionSpace.distance(samples.get(i - 1), samples.get(i)) //
        : samples.Get(i, 0).zero(), i));
    return new TransitionSamplesWrap(samples, spacing);
  }

  private final Tensor samples;
  private final Tensor spacing;

  private TransitionSamplesWrap(Tensor samples, Tensor spacing) {
    this.samples = samples.unmodifiable();
    this.spacing = spacing.unmodifiable();
  }

  public Tensor samples() {
    return samples;
  }

  public Tensor spacing() {
    return spacing;
  }
}
