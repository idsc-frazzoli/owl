// code by jph
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Transpose;

public class So3GroupElement implements LieGroupElement {
  private final Tensor R;

  public So3GroupElement(Tensor R) {
    this.R = R;
  }

  @Override // from LieGroupElement
  public So3GroupElement inverse() {
    // TODO: JPH suggestion OB: is Transpose faster than inverse? (micro optimization)
    return new So3GroupElement(Transpose.of(R));
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
