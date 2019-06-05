// code by ob, jph
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.mat.OrthogonalMatrixQ;

public class So3GroupElement implements LieGroupElement {
  public static So3GroupElement of(Tensor matrix) {
    return new So3GroupElement(OrthogonalMatrixQ.require(matrix));
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

  // Source: http://ethaneade.com/lie.pdf
  @Override // from LieGroupElement
  public Tensor adjoint(Tensor tensor) {
    return matrix.dot(tensor);
  }
}
