// code by mcp
package ch.ethz.idsc.owl.bot.se2.pid;

import ch.ethz.idsc.tensor.Tensor;

public interface Se2UnitConverter {
  /** @param unitless Se2 element
   * @return Se2 element in SI units */
  Tensor toSI(Tensor tensor);
}
