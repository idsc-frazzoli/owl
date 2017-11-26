// code by jph
package ch.ethz.idsc.owl.math;

import java.io.Serializable;
import java.util.function.Function;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** implicit function f:Tensor -> Scalar
 * 
 * implicit functions define regions via {x | f(x) < 0 or f(x) > 0} */
public interface TensorScalarFunction extends Function<Tensor, Scalar>, Serializable {
  // ---
}
