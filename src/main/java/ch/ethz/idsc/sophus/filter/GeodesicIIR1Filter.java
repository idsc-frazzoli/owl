// code by jph
package ch.ethz.idsc.sophus.filter;

import java.util.Objects;

import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/** filter blends extrapolated value with measurement */
public class GeodesicIIR1Filter implements TensorUnaryOperator {
  private final GeodesicInterface geodesicInterface;
  private final Scalar alpha;
  // ---
  private Tensor p = null;

  /** @param geodesicInterface
   * @param alpha */
  public GeodesicIIR1Filter(GeodesicInterface geodesicInterface, Scalar alpha) {
    this.geodesicInterface = geodesicInterface;
    this.alpha = alpha;
  }

  /** @param geodesicInterface
   * @param alpha
   * @param p */
  public GeodesicIIR1Filter(GeodesicInterface geodesicInterface, Scalar alpha, Tensor p) {
    this(geodesicInterface, alpha);
    this.p = p.copy();
  }

  @Override
  public Tensor apply(Tensor tensor) {
    p = Objects.isNull(p) //
        ? tensor.copy()
        : geodesicInterface.split(p, tensor, alpha);
    return p.copy();
  }
}
