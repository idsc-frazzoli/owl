// code by jph
package ch.ethz.idsc.owl.math.sample;

import ch.ethz.idsc.tensor.Tensor;

public class ConstantRandomSample implements RandomSampleInterface {
  private final Tensor tensor;

  /** @param tensor */
  public ConstantRandomSample(Tensor tensor) {
    this.tensor = tensor.unmodifiable();
  }

  @Override // from RandomSampleInterface
  public Tensor randomSample() {
    return tensor;
  }
}
