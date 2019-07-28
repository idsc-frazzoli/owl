// code by gjoel
package ch.ethz.idsc.owl.rrts.core;

import ch.ethz.idsc.tensor.Tensor;

public class TransitionWrap {
  private final Tensor samples;
  private final Tensor spacing;

  public TransitionWrap(Tensor samples, Tensor spacing) {
    // TODO GJOEL check that length of samples and spacing are consistent
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
