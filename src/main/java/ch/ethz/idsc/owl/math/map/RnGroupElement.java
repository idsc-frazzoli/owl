// code by jph
package ch.ethz.idsc.owl.math.map;

import java.io.Serializable;

import ch.ethz.idsc.tensor.Tensor;

/**  */
public class RnGroupElement implements LieGroupAction, Serializable {
  private final Tensor tensor;

  public RnGroupElement(Tensor tensor) {
    this.tensor = tensor;
  }

  @Override // from LieGroupAction
  public LieGroupAction inverse() {
    return new RnGroupElement(tensor.negate());
  }

  @Override // from LieGroupAction
  public Tensor combine(Tensor tensor) {
    return this.tensor.add(tensor);
  }
}
