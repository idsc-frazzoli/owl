// code by jph
package ch.ethz.idsc.owl.math.noise;

import java.io.Serializable;
import java.util.function.Function;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** maps given tensor to a scalar noise value
 * result should depend continuously on input */
public interface ContinuousNoise extends Function<Tensor, Scalar>, Serializable {
  // ---
}
