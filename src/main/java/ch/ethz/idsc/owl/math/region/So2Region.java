// code by jph
package ch.ethz.idsc.owl.math.region;

import java.io.Serializable;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Mod;
import ch.ethz.idsc.tensor.sca.Ramp;
import ch.ethz.idsc.tensor.sca.Sign;

public class So2Region extends ImplicitFunctionRegion<Tensor> implements //
    RegionWithDistance<Tensor>, Serializable {
  private final Scalar center;
  private final Scalar radius;
  private final Mod mod;

  /** @param center angular destination
   * @param radius tolerance */
  public So2Region(Scalar center, Scalar radius) {
    this(center, radius, RealScalar.of(Math.PI));
  }

  public So2Region(Scalar center, Scalar radius, Scalar half_circumference) {
    this.center = center;
    this.radius = Sign.requirePositiveOrZero(radius);
    mod = Mod.function( //
        half_circumference.add(half_circumference), // 2*half_circumference
        half_circumference.negate());
  }

  @Override // from SignedDistanceFunction<Tensor>
  public Scalar signedDistance(Tensor x) {
    return mod.apply(x.Get().subtract(center)).abs().subtract(radius);
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
