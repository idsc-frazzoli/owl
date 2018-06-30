// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public interface GeodesicInterface {
  /** @param p
   * @param q
   * @param scalar any real number
   * @return point on geodesic that connects p and q at parameter scalar
   * for scalar == 0 the function returns p, for scalar == 1 the function returns q */
  Tensor split(Tensor p, Tensor q, Scalar scalar);
}
