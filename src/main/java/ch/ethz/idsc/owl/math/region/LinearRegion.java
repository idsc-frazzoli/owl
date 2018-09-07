// code by ynager
package ch.ethz.idsc.owl.math.region;

import java.io.Serializable;
import java.util.Objects;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Ramp;
import ch.ethz.idsc.tensor.sca.Sign;

public class LinearRegion extends ImplicitFunctionRegion<Tensor> implements //
    RegionWithDistance<Tensor>, Serializable {
  // ---
  private final Scalar center;
  private final Scalar radius;

  /** @param center angular destination, non-null
   * @param radius non-negative tolerance
   * @throws Exception if either input parameter violates specifications */
  public LinearRegion(Scalar center, Scalar radius) {
    this.center = Objects.requireNonNull(center);
    this.radius = Sign.requirePositiveOrZero(radius);
  }

  @Override // from SignedDistanceFunction<Tensor>
  public Scalar signedDistance(Tensor x) {
    return center.subtract(x).abs().subtract(radius);
  }

  @Override // from DistanceFunction<Tensor>
  public Scalar distance(Tensor x) {
    return Ramp.FUNCTION.apply(signedDistance(x));
  }

  public Scalar center() {
    return center;
  }

  public Scalar radius() {
    return radius;
  }
}
