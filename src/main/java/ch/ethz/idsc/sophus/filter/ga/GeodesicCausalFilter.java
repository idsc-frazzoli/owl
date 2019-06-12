// code by jph
package ch.ethz.idsc.sophus.filter.ga;

import java.util.Objects;

import ch.ethz.idsc.sophus.filter.bm.BiinvariantMeanCenter;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

public class GeodesicCausalFilter implements TensorUnaryOperator {
  /** Hint: the following tensorUnaryOperator are typically used
   * {@link GeodesicCenter}, and {@link BiinvariantMeanCenter}
   * 
   * @param tensorUnaryOperator
   * @return
   * @throws Exception if given tensorUnaryOperator is null */
  public static TensorUnaryOperator of(TensorUnaryOperator tensorUnaryOperator) {
    return new GeodesicCausalFilter(Objects.requireNonNull(tensorUnaryOperator));
  }

  // ---
  private final TensorUnaryOperator tensorUnaryOperator;

  private GeodesicCausalFilter(TensorUnaryOperator tensorUnaryOperator) {
    this.tensorUnaryOperator = tensorUnaryOperator;
  }

  @Override // from TensorUnaryOperator
  public Tensor apply(Tensor tensor) {
    return Tensor.of(tensor.stream().map(xya -> tensorUnaryOperator.apply(xya)));
  }
}
