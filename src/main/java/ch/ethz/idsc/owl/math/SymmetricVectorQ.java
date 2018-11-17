// code by jph
package ch.ethz.idsc.owl.math;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.alg.Reverse;
import ch.ethz.idsc.tensor.alg.VectorQ;

public enum SymmetricVectorQ {
  ;
  /** @param tensor
   * @return true if given tensor is a vector invariant under mirroring */
  public static boolean of(Tensor tensor) {
    return VectorQ.of(tensor) && Reverse.of(tensor).equals(tensor);
  }

  /** @param vector
   * @return
   * @throws Exception if given vector is not a symmetric vector */
  public static Tensor require(Tensor vector) {
    if (!of(vector))
      throw TensorRuntimeException.of(vector);
    return vector;
  }
}
