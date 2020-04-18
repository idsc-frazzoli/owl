// code by jph
package ch.ethz.idsc.owl.math.noise;

import ch.ethz.idsc.tensor.opt.TensorScalarFunction;

/** maps given tensor to a scalar noise value
 * result should depend continuously on input */
@FunctionalInterface
public interface ContinuousNoise extends TensorScalarFunction {
  // ---
}
