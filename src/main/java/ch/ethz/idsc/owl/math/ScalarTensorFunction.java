// code by jph
package ch.ethz.idsc.owl.math;

import java.io.Serializable;
import java.util.function.Function;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** serializable interface for functions that map a {@link Scalar} to a {@link Tensor} */
public interface ScalarTensorFunction extends Function<Scalar, Tensor>, Serializable {
  // ---
}
