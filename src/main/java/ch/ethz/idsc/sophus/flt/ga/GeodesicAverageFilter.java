// code by ob
package ch.ethz.idsc.sophus.flt.ga;

import java.util.Objects;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

// TODO OB class is not used, untested
// TODO JPH OWL 045 possibly remove
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
  private final int weightLength;

  private GeodesicAverageFilter(TensorUnaryOperator geodesicAverage, int weightLength) {
    this.geodesicAverage = geodesicAverage;
    this.weightLength = weightLength;
  }

  @Override
  public Tensor apply(Tensor tensor) {
    Tensor result = Unprotect.empty(tensor.length());
    for (int index = 0; index < tensor.length(); ++index)
      result.append(tensor.length() - weightLength < index || index < weightLength //
          ? tensor.get(index)
          : geodesicAverage.apply(tensor.extract(index, index + weightLength)));
    return result;
  }
}
