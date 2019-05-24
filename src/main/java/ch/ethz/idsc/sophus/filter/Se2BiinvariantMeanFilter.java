// code by jph /ob
package ch.ethz.idsc.sophus.filter;

import java.util.Objects;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

public class Se2BiinvariantMeanFilter implements TensorUnaryOperator {
  /** @param seBiinvariantMeanCenter
   * @param radius
   * @return
   * @throws Exception if given geodesicCenter is null */
  public static TensorUnaryOperator of(TensorUnaryOperator seBiinvariantMeanCenter, int radius) {
    return new Se2BiinvariantMeanFilter(Objects.requireNonNull(seBiinvariantMeanCenter), radius);
  }

  // ---
  private final TensorUnaryOperator seBiinvariantMeanCenter;
  private final int radius;

  private Se2BiinvariantMeanFilter(TensorUnaryOperator seBiinvariantMeanCenter, int radius) {
    this.seBiinvariantMeanCenter = seBiinvariantMeanCenter;
    this.radius = radius;
  }

  @Override
  public Tensor apply(Tensor tensor) {
    Tensor result = Tensors.empty();
    for (int index = 0; index < tensor.length(); ++index) {
      int lo = Math.max(0, index - radius);
      int hi = Math.min(index + radius, tensor.length() - 1);
      int delta = Math.min(index - lo, hi - index);
      result.append(seBiinvariantMeanCenter.apply(tensor.extract(index - delta, index + delta + 1)));
    }
    return result;
  }
}
