// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public interface GeodesicInterface {
  /** @param p
   * @param q
   * @param scalar any real number
   * @return */
  Tensor split(Tensor p, Tensor q, Scalar scalar);
}
