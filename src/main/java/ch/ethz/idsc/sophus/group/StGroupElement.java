// code by ob
package ch.ethz.idsc.sophus.group;

import java.io.Serializable;

import ch.ethz.idsc.tensor.RealScalar;
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
public class StGroupElement implements LieGroupElement, Serializable {
  private final Scalar lambda;
  private final Tensor t;

  /** @param lambda_t of the form {lambda, t}
   * @throws Exception if lambda is not positive */
  public StGroupElement(Tensor lambda_t) {
    this( //
        lambda_t.Get(0), //
        lambda_t.get(1));
  }

  private StGroupElement(Scalar lambda, Tensor t) {
    this.lambda = Sign.requirePositive(lambda);
    this.t = t;
  }

  @Override // from LieGroupElement
  public StGroupElement inverse() {
    return new StGroupElement( //
        RealScalar.ONE.divide(lambda), //
        t.divide(lambda).negate());
  }

  @Override // from LieGroupElement
  public Tensor combine(Tensor lambda_t) {
    StGroupElement stGroupElement = new StGroupElement(lambda_t);
    return Tensors.of( //
        stGroupElement.lambda.multiply(lambda), //
        stGroupElement.t.multiply(lambda).add(t));
  }

  // function for convenience and testing
  public Tensor toTensor() {
    return Tensors.of(lambda, t);
  }
}
