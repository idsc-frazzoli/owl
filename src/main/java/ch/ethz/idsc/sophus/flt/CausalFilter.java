// code by jph
package ch.ethz.idsc.sophus.flt;

import java.util.Objects;

import ch.ethz.idsc.sophus.util.SerializableSupplier;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

public class CausalFilter implements TensorUnaryOperator {
  public static TensorUnaryOperator of(SerializableSupplier<TensorUnaryOperator> supplier) {
    return new CausalFilter(Objects.requireNonNull(supplier));
  }

  // ---
  private final SerializableSupplier<TensorUnaryOperator> supplier;

  private CausalFilter(SerializableSupplier<TensorUnaryOperator> supplier) {
    this.supplier = supplier;
  }

  @Override
  public Tensor apply(Tensor tensor) {
    return Tensor.of(tensor.stream().map(supplier.get()));
  }
}
