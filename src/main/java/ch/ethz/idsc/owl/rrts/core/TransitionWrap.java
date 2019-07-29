// code by gjoel
package ch.ethz.idsc.owl.rrts.core;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.VectorQ;

public class TransitionWrap {
  private final Tensor samples;
  private final Tensor spacing;

  public TransitionWrap(Tensor samples, Tensor spacing) {
    this.samples = samples.unmodifiable();
    this.spacing = VectorQ.requireLength(spacing.unmodifiable(), samples.length());
  }

  public Tensor samples() {
    return samples;
  }

  public Tensor spacing() {
    return spacing;
  }
}
