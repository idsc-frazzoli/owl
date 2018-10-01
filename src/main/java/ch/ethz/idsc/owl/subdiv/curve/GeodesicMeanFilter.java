// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

public class GeodesicMeanFilter implements TensorUnaryOperator {
  private final GeodesicMean geodesicMean;
  private final int radius;

  public GeodesicMeanFilter(GeodesicInterface geodesicInterface, int radius) {
    geodesicMean = new GeodesicMean(geodesicInterface);
    this.radius = radius;
  }

  @Override
  public Tensor apply(Tensor tensor) {
    Tensor result = Tensors.empty();
    for (int index = 0; index < tensor.length(); ++index) {
      int lo = Math.max(0, index - radius);
      int hi = Math.min(index + radius, tensor.length() - 1);
      int delta = Math.min(index - lo, hi - index);
      result.append(geodesicMean.apply(tensor.extract(index - delta, index + delta + 1)));
    }
    return result;
  }
}
