// code by jph
package ch.ethz.idsc.sophus.filter;

import java.util.Objects;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/** Hint: the following center operators are typically used
 * {@link GeodesicCenter}
 * {@link BiinvariantMeanCenter} */
public class CenterFilter implements TensorUnaryOperator {
  /** @param geodesicCenter
   * @param radius
   * @return
   * @throws Exception if given geodesicCenter is null */
  public static TensorUnaryOperator of(TensorUnaryOperator geodesicCenter, int radius) {
    return new CenterFilter(Objects.requireNonNull(geodesicCenter), radius);
  }

  // ---
  private final TensorUnaryOperator geodesicCenter;
  private final int radius;

  private CenterFilter(TensorUnaryOperator geodesicCenter, int radius) {
    this.geodesicCenter = geodesicCenter;
    this.radius = radius;
  }

  @Override
  public Tensor apply(Tensor tensor) {
    Tensor result = Tensors.empty();
    for (int index = 0; index < tensor.length(); ++index) {
      int lo = Math.max(0, index - radius);
      int hi = Math.min(index + radius, tensor.length() - 1);
      int delta = Math.min(index - lo, hi - index);
      result.append(geodesicCenter.apply(tensor.extract(index - delta, index + delta + 1)));
    }
    return result;
  }
}
