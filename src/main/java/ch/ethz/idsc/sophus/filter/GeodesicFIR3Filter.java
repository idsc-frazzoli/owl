// code by ob
package ch.ethz.idsc.sophus.filter;

import java.util.Objects;

import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/** filter blends extrapolated value with measurement */
public class GeodesicFIR3Filter implements TensorUnaryOperator {
  // ---
  private final GeodesicInterface geodesicInterface;
  private final Scalar alpha;
  private final Scalar beta;
  private final Scalar gamma;
  // ---
  private Tensor p = null;
  private Tensor q = null;
  private Tensor r = null;

  /** This FIR3 filter uses the following procedure for prediction
   * [[p,q]_beta, r]_gamma **/
  public GeodesicFIR3Filter(GeodesicInterface geodesicInterface, Scalar alpha, Scalar beta) {
    this.geodesicInterface = geodesicInterface;
    this.alpha = alpha;
    this.beta = beta;
    this.gamma = RealScalar.ONE.add((RealScalar.ONE.divide(RealScalar.of(2).subtract(alpha))));
  }

  public GeodesicFIR3Filter(GeodesicInterface geodesicInterface, Scalar alpha, Scalar beta, Tensor p, Tensor q, Tensor r) {
    this(geodesicInterface, alpha, beta);
    this.p = p;
    this.q = q;
    this.r = r;
  }

  /** @return extrapolated "best guess" value from the previous predictions */
  public synchronized Tensor extrapolate() {
    if (Objects.isNull(p))
      return q;
    return geodesicInterface.split(geodesicInterface.split(p, q, beta), r, gamma);
  }

  @Override
  public synchronized Tensor apply(Tensor tensor) {
    if (Objects.isNull(r)) {
      if (Objects.isNull(q)) {
        q = tensor.copy();
        r = tensor.copy();
        return r;
      }
      r = tensor.copy();
      return r;
    }
    Tensor result = geodesicInterface.split(extrapolate(), tensor, alpha);
    p = q.copy();
    q = r.copy();
    r = tensor.copy();
    return result;
  }
}
