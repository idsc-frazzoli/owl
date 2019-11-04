// code by jph
package ch.ethz.idsc.sophus.math;

import java.io.Serializable;
import java.util.function.Function;

import ch.ethz.idsc.tensor.Tensor;

/** serializable function that maps an integer to a tensor
 * 
 * interface can be used to conveniently cast a function to a serializable function.
 * preferably, the interface should not be used as type inside a class. */
@FunctionalInterface
public interface IntegerTensorFunction extends Function<Integer, Tensor>, Serializable {
  // ---
}
