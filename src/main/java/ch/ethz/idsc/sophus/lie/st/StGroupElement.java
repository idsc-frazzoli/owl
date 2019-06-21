// code by ob
package ch.ethz.idsc.sophus.lie.st;

import java.io.Serializable;

import ch.ethz.idsc.sophus.lie.LieGroupElement;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Sign;

/** element of (n + 1)-dimensional Scaling and Translations group
 * 
 * <p>the neutral element is {1, {0, 0, ..., 0}}
 * 
 * <p>Reference:
 * Bi-invariant Means in Lie Groups. Application to Left-invariant Polyaffine Transformations.
 * by Vincent Arsigny, Xavier Pennec, Nicholas Ayache, pp. 27-31, 2006
 * 
 * "Exponential Barycenters of the Canonical Cartan Connection and Invariant Means on Lie Groups"
 * by Xavier Pennec, Vincent Arsigny, p.27, Section 4.1, 2012:
 * 
 * @see StGroup */
public class StGroupElement implements LieGroupElement, Serializable {
  private final Scalar lambda;
  private final Tensor t;

  /** @param lambda_t of the form {lambda, t}
   * @throws Exception if lambda is not strictly positive */
  public StGroupElement(Tensor lambda_t) {
    this(Sign.requirePositive(lambda_t.Get(0)), lambda_t.get(1));
  }

  private StGroupElement(Scalar lambda, Tensor t) {
    this.lambda = lambda;
    this.t = t;
  }

  @Override // from LieGroupElement
  public StGroupElement inverse() {
    return new StGroupElement( //
        lambda.reciprocal(), //
        t.divide(lambda.negate()));
  }

  @Override // from LieGroupElement
  public Tensor combine(Tensor lambda_t) {
    StGroupElement stGroupElement = new StGroupElement(lambda_t);
    return Tensors.of( //
        stGroupElement.lambda.multiply(lambda), //
        stGroupElement.t.multiply(lambda).add(t));
  }

  @Override // from LieGroupElement
  public Tensor adjoint(Tensor tensor) {
    return Tensors.of( //
        tensor.get(0), //
        tensor.get(1).multiply(lambda).subtract(t.multiply(tensor.Get(0))));
  }

  // function for convenience and testing
  public Tensor toTensor() {
    return Tensors.of(lambda, t);
  }
}
