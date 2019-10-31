// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import ch.ethz.idsc.tensor.Tensor;

@FunctionalInterface
public interface TripleCenter {
  /** @param p
   * @param q
   * @param r
   * @return */
  Tensor midpoint(Tensor p, Tensor q, Tensor r);
}
