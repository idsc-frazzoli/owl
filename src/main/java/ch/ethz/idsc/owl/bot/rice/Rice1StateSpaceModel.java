// code by jph
package ch.ethz.idsc.owl.bot.rice;

import java.io.Serializable;

import ch.ethz.idsc.owl.math.model.StateSpaceModel;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Exp;
import ch.ethz.idsc.tensor.sca.Sign;

/** Important:
 * The use of {@link Duncan1StateSpaceModel} is preferred and
 * supports the use of units.
 * 
 * <p>Rice1StateSpaceModel is a single integrator with friction.
 * Rice1StateSpaceModel is unit less.
 * 
 * The implementation for n-dimensional velocity
 * 
 * theory tells that:
 * lipschitz const is 2-norm of 2x2 state space matrix
 * L 0
 * 0 L
 * where L == lambda
 * confirmed with Mathematica
 * 
 * | f(x_1, u) - f(x_2, u) | <= L | x_1 - x_2 |
 * Lipschitz L == |lambda| */
public class Rice1StateSpaceModel implements StateSpaceModel, Serializable {
  /** @param mu
   * @return */
  public static StateSpaceModel of(Scalar mu) {
    return new Rice1StateSpaceModel(Exp.of(mu));
  }

  /***************************************************/
  private final Scalar lambda;

  /** @param lambda strictly positive friction coefficient */
  private Rice1StateSpaceModel(Scalar lambda) {
    this.lambda = Sign.requirePositive(lambda);
  }

  @Override // from StateSpaceModel
  public Tensor f(Tensor x, Tensor u) {
    Tensor v = x;
    return u.subtract(v).multiply(lambda);
  }
}
