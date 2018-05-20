// code by jph
package ch.ethz.idsc.owl.math.sample;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.VectorQ;

public class ConstantRandomSample implements RandomSampleInterface {
  private final Tensor tensor;

  public ConstantRandomSample(Tensor sample) {
    tensor = VectorQ.require(sample).unmodifiable();
  }

  @Override // from RandomSampleInterface
  public Tensor randomSample() {
    return tensor;
  }
}
