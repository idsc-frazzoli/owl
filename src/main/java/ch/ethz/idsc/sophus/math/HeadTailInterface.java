// code by jph
package ch.ethz.idsc.sophus.math;

import ch.ethz.idsc.tensor.Scalar;

public interface HeadTailInterface {
  /** @return function value at head of domain */
  Scalar head();

  /** @return function value at tail of domain */
  Scalar tail();
}
