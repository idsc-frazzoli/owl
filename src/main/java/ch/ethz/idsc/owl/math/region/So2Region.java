// code by jph
package ch.ethz.idsc.owl.math.region;

import java.io.Serializable;
import java.util.Objects;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Mod;
import ch.ethz.idsc.tensor.sca.Ramp;
import ch.ethz.idsc.tensor.sca.Sign;

/** region describes a section of the unit circle */
public final class So2Region extends ImplicitFunctionRegion<Tensor> implements //
    RegionWithDistance<Tensor>, Serializable {
  private static final Scalar PI = RealScalar.of(Math.PI);
  // ---
  private final Scalar center;
  private final Scalar radius;
  private final Mod mod;

  /** @param center angular destination
   * @param radius tolerance */
  public So2Region(Scalar center, Scalar radius) {
    this(center, radius, PI);
  }

  // constructor exists to test with units
  /* package */ So2Region(Scalar center, Scalar radius, Scalar half_circumference) {
    this.center = Objects.requireNonNull(center);
    this.radius = Sign.requirePositiveOrZero(radius);
    mod = Mod.function( //
        half_circumference.add(half_circumference), // 2*half_circumference
        half_circumference.negate());
  }

  @Override // from SignedDistanceFunction<Tensor>
  public Scalar signedDistance(Tensor x) {
    return mod.apply(center.subtract(x)).abs().subtract(radius);
  }

  @Override // from DistanceFunction<Tensor>
  public Scalar distance(Tensor x) {
    return Ramp.FUNCTION.apply(signedDistance(x));
  }

  /** @return center angle of region on unit circle */
  public Scalar center() {
    return center;
  }

  /** @return angular radius of region on unit circle */
  public Scalar radius() {
    return radius;
  }
}
