// code by ob
package ch.ethz.idsc.sophus.filter;

import java.util.Objects;

import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.math.SmoothingKernel;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

public class TangentSpaceFIRnFilter implements TensorUnaryOperator {
  /** @param geodesicDisply non-null
   * @param function non-null
   * @param radius
   * @param alpha
   * @return
   * @throws Exception if either parameter is null */
  public static TensorUnaryOperator of(GeodesicDisplay geodesicDisplay, SmoothingKernel smoothingKernel, int radius, Scalar alpha) {
    return new TangentSpaceFIRnFilter( //
        Objects.requireNonNull(geodesicDisplay), //
        Objects.requireNonNull(smoothingKernel), //
        radius, //
        Objects.requireNonNull(alpha));
  }

  // ---
  private final SmoothingKernel smoothingKernel;
  private final int radius;
  private final Scalar alpha;
  private final GeodesicDisplay geodesicDisplay;

  private TangentSpaceFIRnFilter(GeodesicDisplay geodesicDisplay, SmoothingKernel smoothingKernel, int radius, Scalar alpha) {
    this.geodesicDisplay = geodesicDisplay;
    this.smoothingKernel = smoothingKernel;
    this.radius = radius;
    this.alpha = alpha;
  }

  @Override
  public Tensor apply(Tensor tensor) {
    return Tensor.of(tensor.stream() //
        .map(new TangentSpaceFIRn(geodesicDisplay, smoothingKernel, radius, alpha)));
  }
}