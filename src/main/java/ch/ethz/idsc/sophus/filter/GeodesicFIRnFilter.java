// code by ob
package ch.ethz.idsc.sophus.filter;

import java.util.Objects;

import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

public class GeodesicFIRnFilter implements TensorUnaryOperator {
  /** @param geodesicExtrapolation
   * @param geodesicInterface
   * @param radius
   * @param alpha
   * @return
   * @throws Exception if either parameter is null */
  public static TensorUnaryOperator of(TensorUnaryOperator geodesicExtrapolation, GeodesicInterface geodesicInterface, int radius, Scalar alpha) {
    return new GeodesicFIRnFilter( //
        Objects.requireNonNull(geodesicExtrapolation), //
        Objects.requireNonNull(geodesicInterface), //
        radius, //
        Objects.requireNonNull(alpha));
  }

  // ---
  private final TensorUnaryOperator geodesicExtrapolation;
  private final GeodesicInterface geodesicInterface;
  private final int radius;
  private final Scalar alpha;

  private GeodesicFIRnFilter(TensorUnaryOperator geodesicExtrapolation, GeodesicInterface geodesicInterface, int radius, Scalar alpha) {
    this.geodesicExtrapolation = geodesicExtrapolation;
    this.geodesicInterface = geodesicInterface;
    this.radius = radius;
    this.alpha = alpha;
  }

  @Override
  public Tensor apply(Tensor tensor) {
    return Tensor.of(tensor.stream() //
        .map(new FIRnFilter(geodesicExtrapolation, geodesicInterface, radius, alpha)));
  }
}