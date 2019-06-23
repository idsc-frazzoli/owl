// code by jph
package ch.ethz.idsc.sophus.filter;

import java.util.function.Supplier;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

public class CausalFilter implements TensorUnaryOperator {
  private final Supplier<TensorUnaryOperator> supplier;

  public CausalFilter(Supplier<TensorUnaryOperator> supplier) {
    this.supplier = supplier;
  }

  @Override
  public Tensor apply(Tensor tensor) {
    return Tensor.of(tensor.stream().map(supplier.get()));
  }
}
