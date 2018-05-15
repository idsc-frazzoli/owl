// code by jph
package ch.ethz.idsc.owl.math;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;

/** helper predicate */
public enum RadiusXY {
  ;
  public static Scalar requireSame(Tensor vector) {
    if (vector.Get(0).equals(vector.Get(1)))
      return vector.Get(0);
    throw TensorRuntimeException.of(vector);
  }
}
