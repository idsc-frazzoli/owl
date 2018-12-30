// code by ob
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Exp;
import ch.ethz.idsc.tensor.sca.Log;

/** References:
 * Bi-invariant Means in Lie Groups. Application to Left-invariant Polyaffine Transformations.
 * Vincent Arsigny — Xavier Pennec — Nicholas Ayache */
public enum St1CoveringExponential implements LieExponential {
  INSTANCE;
  // ---
  /** maps a vector x from the Lie-algebra st1 to a vector of the Lie-group ST2
   * 
   * @param x element in the st1 Lie-algebra of the form {dlambda, dt}
   * @return element g in ST1 as vector with coordinates of g == exp x */
  @Override // from LieExponential
  public Tensor exp(Tensor x) {
    Scalar dlambda = x.Get(0);
    Scalar dt = x.Get(1);
    if (Scalars.isZero(dlambda)) {
      return Tensors.of(RealScalar.ONE, dt);
    }
    return Tensors.of(Exp.FUNCTION.apply(dlambda), dlambda.divide(dt).multiply((Exp.FUNCTION.apply(dlambda)).subtract(RealScalar.ONE)));
  }

  /** @param g element in the ST1 Lie group of the form {lambda, t}
   * @return element x in the st1 Lie algebra with x == log g, and g == exp x */
  @Override // from LieExponential
  public Tensor log(Tensor g) {
    Scalar lambda = g.Get(0);
    Scalar t = g.Get(1);
    if (Scalars.isZero(lambda)) {
      return Tensors.of(RealScalar.ZERO, t);
    }
    return Tensors.of(Log.FUNCTION.apply(lambda), //
        (Log.FUNCTION.apply(lambda).divide((RealScalar.ONE.subtract(lambda))).multiply(t)));
  }
}
