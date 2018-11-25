// code by jph
package ch.ethz.idsc.owl.math.group;

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
public class LinearGroupElement implements LieGroupElement {
  private final Tensor matrix;

  /** @param matrix square and invertible
   * @throws Exception if given matrix is not square */
  public LinearGroupElement(Tensor matrix) {
    this.matrix = SquareMatrixQ.require(matrix);
  }

  @Override // from LieGroupElement
  public LinearGroupElement inverse() {
    return new LinearGroupElement(Inverse.of(matrix));
  }

  @Override // from LieGroupElement
  public Tensor combine(Tensor tensor) {
    return matrix.dot(tensor);
  }
}
