// code by jph
package ch.ethz.idsc.sophus.group;

import java.io.Serializable;import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Abs;

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

  /** @param lambdat of the form {lambda, t}} */
  
  public St1GroupElement(Tensor lambdat) {
    this( //
        lambdat.Get(0), //
        lambdat.Get(1)); //
  }
  
  private St1GroupElement(Scalar lambda, Scalar t) {
    this.lambda = lambda;
    this.t= t;
  }

  @Override // from LieGroupElement
  public St1GroupElement inverse() {
    return new St1GroupElement( //
        RealScalar.ONE.divide(lambda),
//        lambda.reciprocal(),//
        t.negate().divide(lambda));
  }

  @Override // from LieGroupElement
  public Tensor combine(Tensor lambdat) {
    St1GroupElement St1GroupElement = new St1GroupElement(lambdat);
    return Tensors.of( //
        lambda.multiply(St1GroupElement.lambda), //
        (lambda.multiply(St1GroupElement.t)).add(t));
  }

  // function for convenience and testing
  public Tensor toTensor() {
    return Tensors.of(lambda, t);
  }
}
