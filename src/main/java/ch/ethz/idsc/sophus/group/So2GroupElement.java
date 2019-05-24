// code by ob
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.sca.Mod;

public class So2GroupElement implements LieGroupElement {
  private static final Mod MOD_ANGLE = Mod.function(Pi.TWO, Pi.VALUE.negate());
  private final Scalar alpha;

  public So2GroupElement(Scalar alpha) {
    this.alpha = MOD_ANGLE.apply(alpha);
  }

  @Override // from LieGroupElement
  public So2GroupElement inverse() {
    return new So2GroupElement(alpha.negate());
  }

  @Override // from LieGroupElement
  public Scalar combine(Tensor tensor) {
    return MOD_ANGLE.apply(alpha.subtract(tensor));
  }

  @Override // from LieGroupElement
  public Scalar adjoint(Tensor tensor) {
    return MOD_ANGLE.apply(alpha.add(tensor));
  }
}
