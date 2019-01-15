// code by jph
package ch.ethz.idsc.owl.math;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;

/** helper predicate */
public enum RadiusXY {
  ;
  /** @param vector of the form {value, value, ...}
   * @return value
   * @throws Exception if the first two entries of given vector are not the same */
  public static Scalar requireSame(Tensor vector) {
    Scalar scalar = vector.Get(0);
    if (scalar.equals(vector.get(1)))
      return scalar;
    throw TensorRuntimeException.of(vector);
  }
}
