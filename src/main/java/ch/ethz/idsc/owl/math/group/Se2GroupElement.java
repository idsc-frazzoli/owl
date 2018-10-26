// code by jph
package ch.ethz.idsc.owl.math.group;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Mod;

/** SE(2) is parameterized by R^2 x [-pi,+pi) */
public class Se2GroupElement extends Se2CoveringGroupElement {
  private static final int MOD_INDEX = 2;
  private static final Mod MOD_ANGLE = Mod.function(Math.PI * 2, -Math.PI);

  // ---
  /** @param xya == {px, py, angle} as member of Lie group SE2 */
  public Se2GroupElement(Tensor xya) {
    super(xya);
  }

  private Se2GroupElement(Scalar px, Scalar py, Scalar pa, Scalar ca, Scalar sa) {
    super(px, py, pa, ca, sa);
  }

  /** @param tensor of the form {px, py, angle}
   * @return vector of length 3 */
  @Override // from Se2CoveringGroupElement
  public Tensor combine(Tensor tensor) {
    Tensor xya = super.combine(tensor);
    xya.set(MOD_ANGLE, MOD_INDEX);
    return xya;
  }

  @Override // from Se2CoveringGroupElement
  Se2GroupElement create(Scalar px, Scalar py, Scalar pa, Scalar ca, Scalar sa) {
    return new Se2GroupElement(px, py, pa, ca, sa);
  }
}
