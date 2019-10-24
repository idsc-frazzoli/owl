// code by jph
package ch.ethz.idsc.sophus.crv;

import ch.ethz.idsc.sophus.math.TensorNorm;
import ch.ethz.idsc.tensor.Tensor;

@FunctionalInterface
public interface LineDistance {
  /** Hint: distance function is typically invariant under the order of p and q
   * but the API does not require symmetry
   * 
   * @param p
   * @param q
   * @return distance function between elements and line/geodesic defined from p and q */
  TensorNorm tensorNorm(Tensor p, Tensor q);
}
