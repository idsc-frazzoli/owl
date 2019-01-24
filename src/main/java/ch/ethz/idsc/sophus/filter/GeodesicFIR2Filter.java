// code by ob
package ch.ethz.idsc.sophus.filter;

import java.util.Objects;

import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/** filter blends extrapolated value with measurement */
public class GeodesicFIR2Filter implements TensorUnaryOperator {
  private static final Scalar TWO = RealScalar.of(2);
  // ---
  private final GeodesicInterface geodesicInterface;
  private final Scalar alpha;
  // ---
  private Tensor p = null;
  private Tensor q = null;

  public GeodesicFIR2Filter(GeodesicInterface geodesicInterface, Scalar alpha) {
    this.geodesicInterface = geodesicInterface;
    this.alpha = alpha;
  }

  public GeodesicFIR2Filter(GeodesicInterface geodesicInterface, Scalar alpha, Tensor p, Tensor q) {
    this(geodesicInterface, alpha);
    this.p = p;
    this.q = q;
  }

  /** @return extrapolated "best guess" value from the previous predictions */
  public synchronized Tensor extrapolate() {
    if (Objects.isNull(p))
      return q;
    return geodesicInterface.split(p, q, TWO);
  }

  @Override
  public synchronized Tensor apply(Tensor tensor) {
    if (Objects.isNull(q)) {
      q = tensor.copy();
      return q.copy();
    }
    Tensor result = geodesicInterface.split(extrapolate(), tensor, alpha);
    p = q.copy();
    q = result.copy();
    return result;
  }
}
