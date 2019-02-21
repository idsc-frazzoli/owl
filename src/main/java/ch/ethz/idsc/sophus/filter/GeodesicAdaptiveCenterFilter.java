// code by ob / jph
package ch.ethz.idsc.sophus.filter;

import java.util.Objects;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

public class GeodesicAdaptiveCenterFilter implements TensorUnaryOperator {
  /** @param geodesicCenter
   * @param radius
   * @return
   * @throws Exception if given geodesicCenter is null */
  public static TensorUnaryOperator of(TensorUnaryOperator GeodesicAdaptiveCenter, int radius) {
    return new GeodesicAdaptiveCenterFilter(Objects.requireNonNull(GeodesicAdaptiveCenter), radius);
  }

  // ---
  private final TensorUnaryOperator GeodesicAdaptiveCenter;
  private final int radius;

  private GeodesicAdaptiveCenterFilter(TensorUnaryOperator GeodesicAdaptiveCenter, int radius) {
    this.GeodesicAdaptiveCenter = GeodesicAdaptiveCenter;
    this.radius = radius;
  }

  @Override
  public Tensor apply(Tensor tensor) {
    Tensor result = Tensors.empty();
    for (int index = 0; index < tensor.length(); ++index) {
      int lo = Math.max(0, index - radius);
      int hi = Math.min(index + radius, tensor.length() - 1);
      int delta = Math.min(index - lo, hi - index);
      result.append(GeodesicAdaptiveCenter.apply(tensor.extract(index - delta, index + delta + 1)));
    }
    return result;
  }
}
