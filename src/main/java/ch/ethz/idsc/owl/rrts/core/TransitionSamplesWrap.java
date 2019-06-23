// code by gjoel
package ch.ethz.idsc.owl.rrts.core;

import ch.ethz.idsc.tensor.Tensor;

public class TransitionSamplesWrap {
  private final Tensor samples;
  private final Tensor spacing;

  public TransitionSamplesWrap(Tensor samples, Tensor spacing) {
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

