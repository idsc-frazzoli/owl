// code by jph
package ch.ethz.idsc.tensor.sig;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.alg.Reverse;
import ch.ethz.idsc.tensor.alg.VectorQ;

public enum SymmetricVectorQ {
  ;
  public static boolean of(Tensor tensor) {
    return VectorQ.of(tensor) && Reverse.of(tensor).equals(tensor);
  }

  public static Tensor require(Tensor vector) {
    if (!of(vector))
      throw TensorRuntimeException.of(vector);
    return vector;
  }
}
