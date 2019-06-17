// code by jph
package ch.ethz.idsc.sophus.lie.se2;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/* package */ class Se2AdjointComp implements TensorUnaryOperator {
  private final Tensor matrix;

  /** @param element from Lie Group SE2 as coordinates {x, y, omega} */
  public Se2AdjointComp(Tensor xya) {
    if (xya.length() != 3)
      throw TensorRuntimeException.of(xya);
    matrix = Se2Utils.toSE2Matrix(Tensors.of( //
        xya.get(1), // t2
        xya.get(0).negate(), // -t1
        xya.get(2))); // omega
  }

  @Override
  public Tensor apply(Tensor uvw) {
    return matrix.dot(uvw);
  }
}
