// code by jph
package ch.ethz.idsc.sophus.lie;

import ch.ethz.idsc.tensor.Tensor;

@FunctionalInterface
public interface LieIntegrator {
  /** @param g element of the Lie-group
   * @param x element of the Lie-algebra
   * @return exp_g x */
  Tensor spin(Tensor g, Tensor x);
}
