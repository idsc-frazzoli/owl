// code by jph
package ch.ethz.idsc.owl.math;

import java.io.Serializable;
import java.util.Objects;

import ch.ethz.idsc.sophus.math.TensorDifference;
import ch.ethz.idsc.sophus.math.TensorMetric;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.nrm.Vector2Norm;

// TODO design/naming not good
public class SimpleTensorMetric implements TensorMetric, Serializable {
  private final TensorDifference tensorDifference;

  /** @param tensorDifference that returns a vector */
  public SimpleTensorMetric(TensorDifference tensorDifference) {
    this.tensorDifference = Objects.requireNonNull(tensorDifference);
  }

  @Override // from TensorMetric
  public Scalar distance(Tensor p, Tensor q) {
    return Vector2Norm.of(tensorDifference.difference(p, q));
  }
}
