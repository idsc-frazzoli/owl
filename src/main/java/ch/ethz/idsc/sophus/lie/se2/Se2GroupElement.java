// code by jph
package ch.ethz.idsc.sophus.lie.se2;

import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringGroupElement;
import ch.ethz.idsc.sophus.lie.so2.So2;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** SE(2) is parameterized by R^2 x [-pi,+pi) */
public class Se2GroupElement extends Se2CoveringGroupElement {
  private static final int MOD_INDEX = 2;

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
    xya.set(So2.MOD, MOD_INDEX);
    return xya;
  }

  @Override // from Se2CoveringGroupElement
  protected Se2GroupElement create(Scalar px, Scalar py, Scalar pa, Scalar ca, Scalar sa) {
    return new Se2GroupElement(px, py, pa, ca, sa);
  }

  @Override
  protected Tensor inverseTensor() {
    return super.inverseTensor();
  }
}
