// code by jph
package ch.ethz.idsc.sophus.filter;

import java.util.Objects;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

public class GeodesicExtrapolateFilter implements TensorUnaryOperator {
  /** @param geodesicCenter
   * @param radius
   * @return */
  public static TensorUnaryOperator of(TensorUnaryOperator geodesicExtrapolate, int radius) {
    return new GeodesicExtrapolateFilter(geodesicExtrapolate, radius);
  }

  // ---
  private final TensorUnaryOperator geodesicExtrapolate;
  private final int radius;

  private GeodesicExtrapolateFilter(TensorUnaryOperator geodesicExtrapolate, int radius) {
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