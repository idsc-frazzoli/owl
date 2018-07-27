// code by jph
package ch.ethz.idsc.owl.math.map;

import ch.ethz.idsc.tensor.Tensor;

public interface LieExponential {
  /** @param x in the Lie-algebra
   * @return element g of the Lie-group with x == log g, and g == exp x */
  Tensor exp(Tensor x);

  /** @param g element in the Lie group
   * @return element x in the se2 Lie algebra with x == log g, and g == exp x */
  Tensor log(Tensor g);
}
