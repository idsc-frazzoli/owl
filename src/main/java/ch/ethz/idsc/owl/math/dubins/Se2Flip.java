// code by jph
package ch.ethz.idsc.owl.math.dubins;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/* package */ enum Se2Flip implements TensorUnaryOperator {
  FUNCTION;
  // ---
  @Override
  public Tensor apply(Tensor tensor) {
    return Tensors.of(tensor.Get(0), tensor.Get(1).negate(), tensor.Get(2).negate());
  }
}
