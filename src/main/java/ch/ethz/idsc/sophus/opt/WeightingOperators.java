// code by jph
package ch.ethz.idsc.sophus.opt;

import java.util.Objects;

import ch.ethz.idsc.sophus.math.WeightingInterface;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;

public enum WeightingOperators {
  ;
  public static TensorUnaryOperator wrap(WeightingInterface weightingInterface, Tensor sequence) {
    Objects.requireNonNull(weightingInterface);
    Objects.requireNonNull(sequence);
    return point -> weightingInterface.weights(sequence, point);
  }
}
