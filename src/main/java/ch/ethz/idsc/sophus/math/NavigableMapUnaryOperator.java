// code by ob, jph
package ch.ethz.idsc.sophus.math;

import java.util.NavigableMap;
import java.util.function.UnaryOperator;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** operator that processes and returns a navigable map */
@FunctionalInterface
public interface NavigableMapUnaryOperator extends UnaryOperator<NavigableMap<Scalar, Tensor>> {
  // ---
}
