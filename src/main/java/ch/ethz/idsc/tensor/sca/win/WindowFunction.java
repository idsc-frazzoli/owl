// code by jph
package ch.ethz.idsc.tensor.sca.win;

import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** window function that define kernels for smoothing of signals using linear convolution */
public interface WindowFunction extends ScalarUnaryOperator {
  /** @return true if function at 1/2 should not be used in discrete smoothing mask */
  boolean isContinuous();
}
