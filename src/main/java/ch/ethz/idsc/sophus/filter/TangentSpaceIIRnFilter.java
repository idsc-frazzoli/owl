// code by ob
package ch.ethz.idsc.sophus.filter;

import java.util.Objects;

import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.math.SmoothingKernel;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

public class TangentSpaceIIRnFilter implements TensorUnaryOperator {
  /** @param geodesicDisply non-null
   * @param function non-null
   * @param radius
   * @param alpha
   * @return
   * @throws Exception if either parameter is null */
  public static TensorUnaryOperator of(GeodesicDisplay geodesicDisplay, SmoothingKernel smoothingKernel, int radius, Scalar alpha) {
    return new TangentSpaceIIRnFilter( //
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

  private TangentSpaceIIRnFilter(GeodesicDisplay geodesicDisplay, SmoothingKernel smoothingKernel, int radius, Scalar alpha) {
    this.geodesicDisplay = geodesicDisplay;
    this.smoothingKernel = smoothingKernel;
    this.radius = radius;
    this.alpha = alpha;
  }

  @Override
  public Tensor apply(Tensor tensor) {
    return Tensor.of(tensor.stream() //
        .map(new TangentSpaceIIRn(geodesicDisplay, smoothingKernel, radius, alpha)));
  }
}