// code by jph
package ch.ethz.idsc.owl.math;

import ch.ethz.idsc.sophus.TensorMetric;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Norm;

/** metric for vectors */
public enum RnMetric implements TensorMetric {
  INSTANCE;
  // ---
  @Override // from TensorMetric
  public Scalar distance(Tensor p, Tensor q) {
    return Norm._2.between(p, q);
  }
}
