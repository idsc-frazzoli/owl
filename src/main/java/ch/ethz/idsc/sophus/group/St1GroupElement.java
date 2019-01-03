// code by ob
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** ST(1) is parameterized by R^2 */
public class St1GroupElement extends St1CoveringGroupElement {
  // ---
  /** @param lamdbdat == {plambda, pt} as member of Lie group ST1 */
  public St1GroupElement(Tensor lambdat) {
    super(lambdat);
  }

  private St1GroupElement(Scalar plambda, Scalar pt) {
    super(plambda, pt);
  }

  /** @param tensor of the form {plambda, pt}
   * @return vector of length 2 */
  @Override // from St1CoveringGroupElement
  public Tensor combine(Tensor tensor) {
    Tensor lambdat = super.combine(tensor);
    return lambdat;
  }

  @Override // from Se2CoveringGroupElement
  St1GroupElement create(Scalar plambda, Scalar pt) {
    return new St1GroupElement(plambda, pt);
  }
}
