// code by jph
package ch.ethz.idsc.sophus.filter;

import java.util.Objects;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

public class GeodesicAverageFilter implements TensorUnaryOperator {
  /** @param geodesicAverage
   * @param weightlength
   * @return
   * @throws Exception if given geodesicCenter is null */
  public static TensorUnaryOperator of(TensorUnaryOperator geodesicAverage, int weightlength) {
    return new GeodesicAverageFilter(Objects.requireNonNull(geodesicAverage), weightlength);
  }

  // ---
  private final TensorUnaryOperator geodesicAverage;
  private int weightlength;

  private GeodesicAverageFilter(TensorUnaryOperator geodesicAverage, int weightlength) {
    this.geodesicAverage = geodesicAverage;
    this.weightlength = weightlength;
  }

  @Override
  public Tensor apply(Tensor tensor) {
    Tensor result = Tensors.empty();
    for (int index = 0; index < tensor.length(); ++index)
      result.append(tensor.length() - weightlength < index || index < weightlength //
          ? tensor.get(index)
          : geodesicAverage.apply(tensor.extract(index, index + weightlength)));
    return result;
  }
}
