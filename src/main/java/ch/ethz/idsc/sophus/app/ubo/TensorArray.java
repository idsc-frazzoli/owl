// code by jph
package ch.ethz.idsc.sophus.app.ubo;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public enum TensorArray {
  ;
  /** Hint: convert back with {@link Tensors#of(Scalar...)}
   * 
   * @param vector
   * @return
   * @throws Exception if given vector is not a tensor of rank 1 */
  public static Tensor[] ofVector(Tensor vector) {
    return vector.stream().toArray(Tensor[]::new);
  }

  /** Hint: convert back with {@link Tensors#matrix(Scalar[][])}
   * 
   * @param matrix not necessarily with array structure
   * @return
   * @throws Exception if given matrix is not a list of vectors */
  public static Tensor[][] ofMatrix(Tensor matrix) {
    return matrix.stream().map(TensorArray::ofVector).toArray(Tensor[][]::new);
  }
}
