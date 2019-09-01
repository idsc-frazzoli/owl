// code by ob
package ch.ethz.idsc.sophus.lie.so2;

import ch.ethz.idsc.sophus.lie.LieGroupElement;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** Ethan Eade:
 * "Because rotations in the plane commute, the adjoint of SO(2) is the identity function." */
public class So2GroupElement implements LieGroupElement {
  private final Scalar alpha;

  public So2GroupElement(Scalar alpha) {
    this.alpha = alpha;
  }

  @Override // from LieGroupElement
  public So2GroupElement inverse() {
    return new So2GroupElement(alpha.negate());
  }

  @Override // from LieGroupElement
  public Scalar combine(Tensor tensor) {
    return So2.MOD.apply(alpha.add(tensor));
  }

  @Override // from LieGroupElement
  public Scalar adjoint(Tensor tensor) {
    return (Scalar) tensor;
  }
}
