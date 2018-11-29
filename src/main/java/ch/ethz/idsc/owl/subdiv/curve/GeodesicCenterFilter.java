// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import java.util.Objects;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

public class GeodesicCenterFilter implements TensorUnaryOperator {
  /** @param geodesicCenter
   * @param radius
   * @return */
  public static TensorUnaryOperator of(TensorUnaryOperator geodesicCenter, int radius) {
    return new GeodesicCenterFilter(geodesicCenter, radius);
  }

  // ---
  private final TensorUnaryOperator geodesicCenter;
  private final int radius;

  private GeodesicCenterFilter(TensorUnaryOperator geodesicCenter, int radius) {
    this.geodesicCenter = Objects.requireNonNull(geodesicCenter);
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
