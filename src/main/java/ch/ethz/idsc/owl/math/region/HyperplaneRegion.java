// code by jph
package ch.ethz.idsc.owl.math.region;

import java.io.Serializable;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Normalize;

public class HyperplaneRegion extends ImplicitFunctionRegion<Tensor> implements Serializable {
  /** orthogonal is normalized to have Euclidean length 1
   * 
   * @param orthogonal is orthogonal to hyperplane pointing outside
   * @param distanceFromZero needed to reach the region
   * @return */
  public static ImplicitFunctionRegion<Tensor> normalize(Tensor orthogonal, Scalar distanceFromZero) {
    return new HyperplaneRegion(Normalize.of(orthogonal), distanceFromZero);
  }

  // ---
  /** orthogonal to hyperplane pointing outside */
  private final Tensor normal;
  private final Scalar distanceFromZero;

  /** @param normal is normal to hyperplane pointing outside
   * @param distanceFromZero needed to reach the region starting from position (0,...,0)
   * That means, if distanceFromZero is negative, (0,...,0) is inside the region */
  public HyperplaneRegion(Tensor normal, Scalar distanceFromZero) {
    this.normal = normal;
    this.distanceFromZero = distanceFromZero;
  }

  @Override // from SignedDistanceFunction<Tensor>
  public Scalar signedDistance(Tensor x) {
    return distanceFromZero.add(x.dot(normal));
  }
}
