// code by jph
package ch.ethz.idsc.owl.math;

import java.io.Serializable;
import java.util.function.Predicate;

import ch.ethz.idsc.tensor.Tensor;

public interface TensorPredicate extends Predicate<Tensor>, Serializable {
  // ---
}
