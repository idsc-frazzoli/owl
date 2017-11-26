// code by jph
package ch.ethz.idsc.owl.math.sample;

import ch.ethz.idsc.tensor.Tensor;

public class ConstantRandomSample implements RandomSampleInterface {
  private final Tensor tensor;

  public ConstantRandomSample(Tensor sample) {
    tensor = sample.copy().unmodifiable();
  }

  @Override
  public Tensor randomSample() {
    return tensor;
  }
}
