// code by ynager
package ch.ethz.idsc.owl.math.region;

import java.io.Serializable;
import java.util.Objects;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Abs;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Sign;

/** interval region in R^1 */
public class LinearRegion extends ImplicitRegionWithDistance implements Serializable {
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
    return Abs.between(center, (Scalar) x).subtract(radius);
  }

  /** @return center of region */
  public Scalar center() {
    return center;
  }

  /** @return radius of region */
  public Scalar radius() {
    return radius;
  }

  /** @return region as clip interval */
  public Clip clip() {
    return Clips.interval( //
        center.subtract(radius), //
        center.add(radius));
  }
}
