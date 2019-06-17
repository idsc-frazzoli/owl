// code by ob
package ch.ethz.idsc.sophus.filter.ga;

import java.util.Objects;

import ch.ethz.idsc.sophus.math.SplitInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

public class GeodesicIIRnFilter implements TensorUnaryOperator {
  /** @param geodesicExtrapolation
   * @param splitInterface
   * @param radius
   * @param alpha
   * @return
   * @throws Exception if either parameter is null */
  public static TensorUnaryOperator of(TensorUnaryOperator geodesicExtrapolation, SplitInterface splitInterface, int radius, Scalar alpha) {
    return new GeodesicIIRnFilter( //
        Objects.requireNonNull(geodesicExtrapolation), //
        Objects.requireNonNull(splitInterface), //
        radius, //
        Objects.requireNonNull(alpha));
  }

  // ---
  private final TensorUnaryOperator geodesicExtrapolation;
  private final SplitInterface splitInterface;
  private final int radius;
  private final Scalar alpha;

  private GeodesicIIRnFilter(TensorUnaryOperator geodesicExtrapolation, SplitInterface splitInterface, int radius, Scalar alpha) {
    this.geodesicExtrapolation = geodesicExtrapolation;
    this.splitInterface = splitInterface;
    this.radius = radius;
    this.alpha = alpha;
  }

  @Override
  public Tensor apply(Tensor tensor) {
    return Tensor.of(tensor.stream() //
        .map(new GeodesicIIRn(geodesicExtrapolation, splitInterface, radius, alpha)));
  }
}