// code by ob /jph
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.mat.Inverse;

public class So2GroupElement implements LieGroupElement {
  private final Tensor R;

  public So2GroupElement(Tensor R) {
    this.R = R;
  }

  @Override // from LieGroupElement
  public So2GroupElement inverse() {
    return new So2GroupElement(Inverse.of(R));
  }

  @Override // from LieGroupElement
  public Tensor combine(Tensor tensor) {
    return R.dot(tensor);
  }

  @Override // from LieGroupElement
  public Tensor adjoint(Tensor tensor) {
    return R.dot(tensor);
  }
}
