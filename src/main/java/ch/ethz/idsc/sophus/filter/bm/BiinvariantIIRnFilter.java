// code by ob
package ch.ethz.idsc.sophus.filter.bm;

import java.util.Objects;

import ch.ethz.idsc.sophus.lie.BiinvariantMean;
import ch.ethz.idsc.sophus.math.win.SmoothingKernel;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

public class BiinvariantIIRnFilter implements TensorUnaryOperator {
  /** @param geodesicDisply non-null
   * @param function non-null
   * @param radius
   * @param alpha
   * @return
   * @throws Exception if either parameter is null */
  public static TensorUnaryOperator of(BiinvariantMean biinvariantMean, SmoothingKernel smoothingKernel, int radius, Scalar alpha) {
    return new BiinvariantIIRnFilter( //
        Objects.requireNonNull(biinvariantMean), //
        Objects.requireNonNull(smoothingKernel), //
        radius, //
        Objects.requireNonNull(alpha));
  }

  // ---
  private final BiinvariantMean biinvariantMean;
  private final SmoothingKernel smoothingKernel;
  private final int radius;
  private final Scalar alpha;

  private BiinvariantIIRnFilter(BiinvariantMean biinvariantMean, SmoothingKernel smoothingKernel, int radius, Scalar alpha) {
    this.biinvariantMean = biinvariantMean;
    this.smoothingKernel = smoothingKernel;
    this.radius = radius;
    this.alpha = alpha;
  }

  @Override
  public Tensor apply(Tensor tensor) {
    return Tensor.of(tensor.stream() //
        .map(new BiinvariantMeanIIRn(biinvariantMean, smoothingKernel, radius, alpha)));
  }
}