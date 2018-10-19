// code by jph
package ch.ethz.idsc.tensor.sig;

import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

public interface WindowFunction extends ScalarUnaryOperator {
  /** @return true if function evaluates to zero at 1/2 */
  boolean isZero();
}
