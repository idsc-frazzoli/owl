// code by jph
package ch.ethz.idsc.owl.math.map;

import java.io.Serializable;

import ch.ethz.idsc.tensor.Tensor;

/**  */
public class RnGroupElement implements LieGroupElement, Serializable {
  private final Tensor tensor;

  public RnGroupElement(Tensor tensor) {
    this.tensor = tensor;
  }

  @Override // from LieGroupElement
  public RnGroupElement inverse() {
    return new RnGroupElement(tensor.negate());
  }

  @Override // from LieGroupElement
  public Tensor combine(Tensor tensor) {
    return this.tensor.add(tensor);
  }
}
