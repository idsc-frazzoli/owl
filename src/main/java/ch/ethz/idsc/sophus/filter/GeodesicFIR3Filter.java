// code by ob
package ch.ethz.idsc.sophus.filter;

import java.util.Objects;

import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/** filter blends extrapolated value with measurement */
// TODO check and more tests
// TODO document oscillations for linear case (recommend alpha)
// TODO GeodesicIIR3Filter for 3nd order estimation
public class GeodesicFIR3Filter implements TensorUnaryOperator {
  private static final Scalar TWO = RealScalar.of(2);
  // ---
  private final GeodesicInterface geodesicInterface;
  private final Scalar alpha;
  // ---
  private Tensor p = null;
  private Tensor q = null;
  private Tensor r = null;
  // gamma 2/3 is taken from vertex inserstion example, 7/4 als komplement. Aber hier ist sicher noch ein fehler
  private Scalar gamma = RationalScalar.of(2, 3);
  private Scalar beta = RationalScalar.of(7, 4);

  public GeodesicFIR3Filter(GeodesicInterface geodesicInterface, Scalar alpha) {
    this.geodesicInterface = geodesicInterface;
    this.alpha = alpha;
  }

  public GeodesicFIR3Filter(GeodesicInterface geodesicInterface, Scalar alpha, Tensor p, Tensor q, Tensor r) {
    this(geodesicInterface, alpha);
    this.p = p;
    this.q = q;
    this.r = r;
  }

  public synchronized Tensor interpolate() {
    if (Objects.isNull(q)) {
      System.out.println(q);
      return q;
    }
    return geodesicInterface.split(p, q, gamma);
  }

  /** @return extrapolated "best guess" value from the previous predictions */
  public synchronized Tensor extrapolate() {
    if (Objects.isNull(p))
      return q;
    return geodesicInterface.split(interpolate(), r, beta);
  }

  @Override
  public synchronized Tensor apply(Tensor tensor) {
    if (Objects.isNull(r)) {
      if (Objects.isNull(q)) {
        q = tensor.copy();
        r = tensor.copy();
        return r;
      } else {
        r = tensor.copy();
        return r;
      }
    }
    Tensor result = geodesicInterface.split(extrapolate(), tensor, alpha);
    p = q.copy();
    q = r.copy();
    r = tensor.copy();
    return result;
  }
}
