// code by ob
package ch.ethz.idsc.sophus.group;

import java.io.Serializable;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** represents an element of the covering group ST(1),
 * which is defined by two real values, or equivalently by a vector from R^2 */
public class St1CoveringGroupElement implements LieGroupElement, Serializable {
  private final Scalar plambda;
  private final Scalar pt;

  /** @param lambdat== {plambda, pt} as member of Lie group St1 */
  public St1CoveringGroupElement(Tensor lambdat) {
    plambda = lambdat.Get(0);
    pt = lambdat.Get(1);
  }

  St1CoveringGroupElement(Scalar plambda, Scalar pt) {
    this.plambda = plambda;
    this.pt = pt;
  }

  @Override // from LieGroupElement
  public final St1CoveringGroupElement inverse() {
    return create( //
        plambda.reciprocal(), //
        pt.negate().divide(plambda));
  }

  /** @param tensor of the form {lambda, t}
   * @return vector of length 2 */
  @Override // from LieGroupElement
  public Tensor combine(Tensor tensor) {
    Scalar qlambda = tensor.Get(0);
    Scalar qt = tensor.Get(1);
    return Tensors.of( //
        plambda.multiply(qlambda), //
        qlambda.multiply(pt).add(qt));
  }

  St1CoveringGroupElement create(Scalar plambda, Scalar pt) {
    return new St1CoveringGroupElement(plambda, pt);
  }
}
