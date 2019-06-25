// code by jph
package ch.ethz.idsc.sophus.filter;

import java.util.Objects;

import ch.ethz.idsc.sophus.filter.bm.BiinvariantMeanCenter;
import ch.ethz.idsc.sophus.filter.ga.GeodesicCenter;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.img.MeanFilter;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/** @see MeanFilter */
public class CenterFilter implements TensorUnaryOperator {
  /** Hint: the following tensorUnaryOperator are typically used
   * {@link GeodesicCenter}, and {@link BiinvariantMeanCenter}
   * 
   * @param tensorUnaryOperator
   * @param radius non-negative
   * @return
   * @throws Exception if given tensorUnaryOperator is null */
  public static TensorUnaryOperator of(TensorUnaryOperator tensorUnaryOperator, int radius) {
    if (radius < 0)
      throw new IllegalArgumentException("" + radius);
    return new CenterFilter(Objects.requireNonNull(tensorUnaryOperator), radius);
  }

  // ---
  private final TensorUnaryOperator tensorUnaryOperator;
  private final int radius;

  private CenterFilter(TensorUnaryOperator tensorUnaryOperator, int radius) {
    this.tensorUnaryOperator = tensorUnaryOperator;
    this.radius = radius;
  }

  @Override // from TensorUnaryOperator
  public Tensor apply(Tensor tensor) {
    Tensor result = Unprotect.empty(tensor.length());
    for (int index = 0; index < tensor.length(); ++index) {
      int lo = Math.max(0, index - radius);
      int hi = Math.min(index + radius, tensor.length() - 1);
      int delta = Math.min(index - lo, hi - index);
      result.append(tensorUnaryOperator.apply(tensor.extract(index - delta, index + delta + 1)));
    }
    return result;
  }
}
