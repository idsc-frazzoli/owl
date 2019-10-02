// code by jph
package ch.ethz.idsc.sophus.math;

import ch.ethz.idsc.tensor.Tensor;

/** implementations are not required to be symmetric.
 * 
 * Example: For clothoids
 * <pre>
 * midpoint(p, q) != midpoint(q, p)
 * </pre> */
@FunctionalInterface
public interface MidpointInterface {
  /** @param p
   * @param q
   * @return midpoint along curve from p to q */
  Tensor midpoint(Tensor p, Tensor q);
}
