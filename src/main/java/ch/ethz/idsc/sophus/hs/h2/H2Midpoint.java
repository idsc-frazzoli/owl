// code by jph
package ch.ethz.idsc.sophus.hs.h2;

import ch.ethz.idsc.sophus.math.MidpointInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Norm2Squared;
import ch.ethz.idsc.tensor.sca.Sqrt;

/** H2 parameterized with coordinates in unit disc
 * Poincaré disk model of hyperbolic geometry
 * 
 * Reference:
 * "Intrinsic Subdivision with Smooth Limits for Graphics and Animation"
 * Wallner, Pottmann, p. 6, eq. (6)
 * 
 * Reference:
 * Alekseevskij et al. 1993 */
public enum H2Midpoint implements MidpointInterface {
  INSTANCE;
  // ---
  @Override // from MidpointInterface
  public Tensor midpoint(Tensor a, Tensor b) {
    Tensor sum = psi(a).add(psi(b));
    return phi(sum.divide(nrm(sum)));
  }

  private static Tensor phi(Tensor x) {
    return x.extract(1, 3).divide(RealScalar.ONE.add(x.Get(0)));
  }

  private static Tensor psi(Tensor x) {
    Scalar x0 = x.Get(0);
    Scalar x1 = x.Get(1);
    Scalar xs = Norm2Squared.ofVector(x);
    return Tensors.of( //
        RealScalar.ONE.add(xs), //
        x0.add(x0), //
        x1.add(x1)).divide(RealScalar.ONE.subtract(xs));
  }

  private static Scalar nrm(Tensor x) {
    Scalar x0 = x.Get(0);
    Scalar x1 = x.Get(1);
    Scalar x2 = x.Get(2);
    return Sqrt.FUNCTION.apply(x0.multiply(x0).subtract(x1.multiply(x1)).subtract(x2.multiply(x2)));
  }
}
