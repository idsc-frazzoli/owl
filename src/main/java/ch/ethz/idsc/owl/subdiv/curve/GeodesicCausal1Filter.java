// code by ob
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.owl.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/** filter blends extrapolated value with measurement */
public class GeodesicCausal1Filter implements TensorUnaryOperator {
  private static final Scalar TWO = RealScalar.of(2);
  // ---
  private final GeodesicInterface geodesicInterface;
  private final Scalar alpha;
  // ---
  private Tensor p;
  private Tensor q;

  public GeodesicCausal1Filter(GeodesicInterface geodesicInterface, Scalar alpha, Tensor p, Tensor q) {
    this.geodesicInterface = geodesicInterface;
    this.alpha = alpha;
    this.p = p;
    this.q = q;
  }

  /** @return extrapolated "best guess" value from the previous predictions */
  public synchronized Tensor extrapolate() {
    return geodesicInterface.split(p, q, TWO);
  }

  @Override
  public synchronized Tensor apply(Tensor tensor) {
    Tensor result = geodesicInterface.split(extrapolate(), tensor, alpha);
    p = q;
    q = tensor;
    return result;
  }
}
