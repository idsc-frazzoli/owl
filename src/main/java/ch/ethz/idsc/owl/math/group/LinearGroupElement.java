// code by jph
package ch.ethz.idsc.owl.math.group;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.mat.Inverse;

public class LinearGroupElement implements LieGroupElement {
  private final Tensor matrix;

  public LinearGroupElement(Tensor matrix) {
    this.matrix = matrix;
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
