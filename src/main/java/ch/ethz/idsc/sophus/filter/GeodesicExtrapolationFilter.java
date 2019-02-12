// code by ob
package ch.ethz.idsc.sophus.filter;

import java.util.Objects;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

public class GeodesicExtrapolationFilter implements TensorUnaryOperator {
  /** @param geodesicExtrapolation
   * @param radius
   * @return */
  public static TensorUnaryOperator of(TensorUnaryOperator geodesicExtrapolation, int radius) {
    return new GeodesicExtrapolationFilter(geodesicExtrapolation, radius);
  }

  // ---
  private final TensorUnaryOperator geodesicExtrapolate;
  private final int radius;

  private GeodesicExtrapolationFilter(TensorUnaryOperator geodesicExtrapolate, int radius) {
    this.geodesicExtrapolate = Objects.requireNonNull(geodesicExtrapolate);
    this.radius = radius;
  }

  @Override
  public Tensor apply(Tensor tensor) {
    Tensor result = Tensors.empty();
    for (int index = 0; index < tensor.length(); ++index) {
      int lo = Math.max(0, index - radius);
      int delta = index - lo;
      result.append(geodesicExtrapolate.apply(tensor.extract(index - delta, index + 1)));
    }
    return result;
  }
}