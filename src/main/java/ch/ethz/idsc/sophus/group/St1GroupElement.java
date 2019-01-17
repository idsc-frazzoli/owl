// code by ob
package ch.ethz.idsc.sophus.group;

import java.io.Serializable;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Sign;

/** element of 1-dimensional Scaling and Translations group
 * 
 * <p>the neutral element is {1,0}
 * 
 * <p>Reference:
 * Bi-invariant Means in Lie Groups.
 * Application to Left-invariant Polyaffine Transformations.
 * by Vincent Arsigny, Xavier Pennec, Nicholas Ayache
 * pages 27-31 */
public class St1GroupElement implements LieGroupElement, Serializable {
  private final Scalar lambda;
  private final Scalar t;

  /** @param lambda_t of the form {lambda, t}}
   * @throws Exception if lambda is not positive */
  public St1GroupElement(Tensor lambda_t) {
    this( //
        lambda_t.Get(0), //
        lambda_t.Get(1));
  }

  private St1GroupElement(Scalar lambda, Scalar t) {
    this.lambda = Sign.requirePositive(lambda);
    this.t = t;
  }

  @Override // from LieGroupElement
  public St1GroupElement inverse() {
    return new St1GroupElement( //
        lambda.reciprocal(), //
        t.divide(lambda).negate());
  }

  @Override // from LieGroupElement
  public Tensor combine(Tensor lambda_t) {
    St1GroupElement st1GroupElement = new St1GroupElement(lambda_t);
    return Tensors.of( //
        st1GroupElement.lambda.multiply(lambda), //
        st1GroupElement.t.multiply(lambda).add(t));
  }

  // function for convenience and testing
  public Tensor toTensor() {
    return Tensors.of(lambda, t);
  }
}
