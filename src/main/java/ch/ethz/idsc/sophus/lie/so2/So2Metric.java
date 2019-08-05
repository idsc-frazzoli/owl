// code by gjoel
package ch.ethz.idsc.sophus.lie.so2;

import ch.ethz.idsc.sophus.math.TensorMetric;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public enum So2Metric implements TensorMetric {
  INSTANCE;
  // ---
  @Override // from TensorMetric
  public Scalar distance(Tensor p, Tensor q) {
    return So2.MOD.apply((Scalar) p.subtract(q)).abs();
  }
}
