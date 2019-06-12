// code by jph
package ch.ethz.idsc.sophus.crv.dubins;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/* package */ enum Se2Flip implements TensorUnaryOperator {
  FUNCTION;
  // ---
  @Override
  public Tensor apply(Tensor xya) {
    return Tensors.of(xya.Get(0), xya.Get(1).negate(), xya.Get(2).negate());
  }
}
