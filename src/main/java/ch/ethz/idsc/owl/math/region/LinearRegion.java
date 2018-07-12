// code by ynager
package ch.ethz.idsc.owl.math.region;

import java.io.Serializable;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Ramp;

public class LinearRegion extends ImplicitFunctionRegion<Tensor> implements //
    RegionWithDistance<Tensor>, Serializable {
  // ---
  private final Scalar center;
  private final Scalar radius;

  /** @param center angular destination
   * @param radius tolerance */
  public LinearRegion(Scalar center, Scalar radius) {
    this.center = center;
    this.radius = radius;
  }

  @Override // from SignedDistanceFunction<Tensor>
  public Scalar signedDistance(Tensor x) {
    return x.Get().subtract(center).abs().subtract(radius);
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
