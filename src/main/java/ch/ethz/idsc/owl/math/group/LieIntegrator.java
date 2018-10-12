// code by jph
package ch.ethz.idsc.owl.math.group;

import ch.ethz.idsc.tensor.Tensor;

public interface LieIntegrator {
  /** @param g element of the Lie-group
   * @param x element of the Lie-algebra
   * @return g . exp x */
  Tensor spin(Tensor g, Tensor x);
}
