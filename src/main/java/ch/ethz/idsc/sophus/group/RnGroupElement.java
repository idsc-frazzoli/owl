// code by jph
package ch.ethz.idsc.sophus.group;

import java.io.Serializable;

import ch.ethz.idsc.tensor.Tensor;

/** represents a vector in Euclidean space
 * with addition as group operation
 * 
 * the adjoint map is the identity for each group element */
public class RnGroupElement implements LieGroupElement, Serializable {
  private final Tensor tensor;

  public RnGroupElement(Tensor vector) {
    this.tensor = vector;
  }

  @Override // from LieGroupElement
  public RnGroupElement inverse() {
    return new RnGroupElement(tensor.negate());
  }

  @Override // from LieGroupElement
  public Tensor combine(Tensor tensor) {
    return this.tensor.add(tensor);
  }

  @Override // from LieGroupElement
  public Tensor adjoint(Tensor tensor) {
    return tensor.copy();
  }
}
