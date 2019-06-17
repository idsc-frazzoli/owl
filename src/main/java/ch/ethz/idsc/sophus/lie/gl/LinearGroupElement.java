// code by jph
package ch.ethz.idsc.sophus.lie.gl;

import java.io.Serializable;

import ch.ethz.idsc.sophus.lie.LieGroupElement;
import ch.ethz.idsc.sophus.lie.so3.So3Geodesic;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.mat.Inverse;
import ch.ethz.idsc.tensor.mat.SquareMatrixQ;

/** Element of the Lie group GL(n) of invertible square matrices.
 * 
 * implementation is not optimized. for instance
 * new LinearGroupElement(a).inverse().combine(b)
 * new LinearGroupElement(LinearSolve.of(a, b))
 * 
 * @see So3Geodesic */
public class LinearGroupElement implements LieGroupElement, Serializable {
  /** @param matrix square and invertible
   * @return
   * @throws Exception if given matrix is not invertible */
  public static LinearGroupElement of(Tensor matrix) {
    return new LinearGroupElement(matrix, Inverse.of(matrix));
  }

  // ---
  private final Tensor matrix;
  private final Tensor inverse;

  private LinearGroupElement(Tensor matrix, Tensor inverse) {
    this.matrix = matrix;
    this.inverse = inverse;
  }

  @Override // from LieGroupElement
  public LinearGroupElement inverse() {
    return new LinearGroupElement(inverse, matrix);
  }

  @Override // from LieGroupElement
  public Tensor combine(Tensor tensor) {
    return matrix.dot(SquareMatrixQ.require(tensor));
  }

  @Override // from LieGroupElement
  public Tensor adjoint(Tensor tensor) {
    return matrix.dot(SquareMatrixQ.require(tensor)).dot(inverse);
  }
}
