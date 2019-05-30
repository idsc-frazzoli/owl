// code by jph
package ch.ethz.idsc.sophus.planar;

import ch.ethz.idsc.tensor.Tensor;

public interface MidpointInterface {
  /** @param p
   * @param q
   * @return midpoint between p and q */
  Tensor midpoint(Tensor p, Tensor q);
}
