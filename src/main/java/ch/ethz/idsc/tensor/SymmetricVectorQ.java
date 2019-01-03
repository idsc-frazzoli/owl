// code by jph
package ch.ethz.idsc.tensor;

import ch.ethz.idsc.tensor.alg.Reverse;
import ch.ethz.idsc.tensor.alg.VectorQ;

/** symmetric vectors are of the form
 * <pre>
 * {a}
 * {a,a}
 * {a,b,a}
 * {a,b,b,a}
 * {a,b,c,b,a}
 * ...
 * </pre> */
public enum SymmetricVectorQ {
  ;
  /** @param tensor
   * @return true if given tensor is a vector invariant under mirroring */
  public static boolean of(Tensor tensor) {
    return VectorQ.of(tensor) && Reverse.of(tensor).equals(tensor);
  }

  /** @param vector
   * @return given vector
   * @throws Exception if given vector is not a symmetric vector */
  public static Tensor require(Tensor vector) {
    if (!of(vector))
      throw TensorRuntimeException.of(vector);
    return vector;
  }
}
