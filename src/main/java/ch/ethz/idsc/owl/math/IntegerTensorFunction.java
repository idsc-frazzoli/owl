// code by jph
package ch.ethz.idsc.owl.math;

import java.io.Serializable;
import java.util.function.Function;

import ch.ethz.idsc.tensor.Tensor;

public interface IntegerTensorFunction extends Function<Integer, Tensor>, Serializable {
  // ---
}