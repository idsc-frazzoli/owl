// code by ob
package ch.ethz.idsc.sophus.filter.ts;

import java.util.Objects;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

public class TangentSpaceFIRnFilter implements TensorUnaryOperator {
  /** @param geodesicDisply non-null
   * @param function non-null
   * @param radius
   * @param alpha
   * @return
   * @throws Exception if either parameter is null */
  public static TensorUnaryOperator of(ScalarUnaryOperator smoothingKernel, int radius, Scalar alpha) {
    return new TangentSpaceFIRnFilter( //
        Objects.requireNonNull(smoothingKernel), //
        radius, //
        Objects.requireNonNull(alpha));
  }

  // ---
  private final ScalarUnaryOperator smoothingKernel;
  private final int radius;
  private final Scalar alpha;

  private TangentSpaceFIRnFilter(ScalarUnaryOperator smoothingKernel, int radius, Scalar alpha) {
    this.smoothingKernel = smoothingKernel;
    this.radius = radius;
    this.alpha = alpha;
  }

  @Override
  public Tensor apply(Tensor tensor) {
    // FIXME OB null below
    return Tensor.of(tensor.stream() //
        .map(new TangentSpaceFIRn(null, smoothingKernel, radius, alpha)));
  }
}