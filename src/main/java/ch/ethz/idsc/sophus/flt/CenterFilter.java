// code by jph
package ch.ethz.idsc.sophus.flt;

import java.util.Objects;

import ch.ethz.idsc.sophus.flt.bm.BiinvariantMeanCenter;
import ch.ethz.idsc.sophus.flt.ga.GeodesicCenter;
import ch.ethz.idsc.tensor.Integers;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.img.ImageFilter;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/** CenterFilter uses <em>odd</em> number of elements from the signal always.
 * 
 * <p>CenterFilter is different towards the boundaries of the signal when
 * compared to a 1-dimensional {@link ImageFilter}.
 * 
 * <p>For instance, CenterFilter for radius 2
 * <pre>
 * out[0] = f[0]
 * out[1] = f[0, 1, 2]
 * out[2] = f[0, 1, 2, 3, 4]
 * out[3] = f[1, 2, 3, 4, 5]
 * ...
 * </pre>
 * 
 * Whereas ImageFilter for radius 2
 * <pre>
 * out[0] = f[0, 1, 2] (not centered at 0)
 * out[1] = f[0, 1, 2, 3] (not centered at 1)
 * out[2] = f[0, 1, 2, 3, 4]
 * out[3] = f[1, 2, 3, 4, 5]
 * ...
 * </pre> */
public class CenterFilter implements TensorUnaryOperator {
  /** Hint: the following tensorUnaryOperator are typically used
   * {@link GeodesicCenter}, and {@link BiinvariantMeanCenter}
   * 
   * @param tensorUnaryOperator
   * @param radius non-negative
   * @return
   * @throws Exception if given tensorUnaryOperator is null */
  public static TensorUnaryOperator of(TensorUnaryOperator tensorUnaryOperator, int radius) {
    return new CenterFilter( //
        Objects.requireNonNull(tensorUnaryOperator), //
        Integers.requirePositiveOrZero(radius));
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
    Tensor result = Tensors.reserve(tensor.length());
    for (int index = 0; index < tensor.length(); ++index) {
      int lo = Math.max(0, index - radius);
      int hi = Math.min(index + radius, tensor.length() - 1);
      int delta = Math.min(index - lo, hi - index);
      result.append(tensorUnaryOperator.apply(tensor.extract(index - delta, index + delta + 1)));
    }
    return result;
  }
}
