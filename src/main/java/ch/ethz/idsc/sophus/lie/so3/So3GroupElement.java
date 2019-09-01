// code by ob, jph
package ch.ethz.idsc.sophus.lie.so3;

import ch.ethz.idsc.sophus.lie.LieGroupElement;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.mat.OrthogonalMatrixQ;

/** Reference: http://ethaneade.com/lie.pdf */
public class So3GroupElement implements LieGroupElement {
  /** @param matrix 3 x 3
   * @return */
  public static So3GroupElement of(Tensor matrix) {
    if (matrix.length() == 3)
      return new So3GroupElement(OrthogonalMatrixQ.require(matrix));
    throw TensorRuntimeException.of(matrix);
  }

  // ---
  private final Tensor matrix;

  public So3GroupElement(Tensor matrix) {
    this.matrix = matrix;
  }

  @Override // from LieGroupElement
  public So3GroupElement inverse() {
    return new So3GroupElement(Transpose.of(matrix));
  }

  @Override // from LieGroupElement
  public Tensor combine(Tensor tensor) {
    return matrix.dot(OrthogonalMatrixQ.require(tensor));
  }

  @Override // from LieGroupElement
  public Tensor adjoint(Tensor tensor) {
    return matrix.dot(tensor);
  }
}
