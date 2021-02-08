// code by jph
package ch.ethz.idsc.owl.bot.rice;

import java.io.Serializable;

import ch.ethz.idsc.owl.math.model.StateSpaceModel;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.sca.Sign;

/** double integrator with friction
 * 
 * implementation for n-dimensional velocity
 * 
 * | f(x_1, u) - f(x_2, u) | <= L | x_1 - x_2 |
 * Lipschitz L == Hypot.of(RealScalar.ONE, lambda) */
public class Duncan2StateSpaceModel implements StateSpaceModel, Serializable {
  // ---
  private final Scalar lambda;

  /** @param lambda non-negative friction coefficient typically with unit [s^-1] */
  public Duncan2StateSpaceModel(Scalar lambda) {
    this.lambda = Sign.requirePositiveOrZero(lambda);
  }

  @Override // from StateSpaceModel
  public Tensor f(Tensor x, Tensor u) {
    Tensor v = x.extract(u.length(), x.length());
    return Join.of(v, u.subtract(v.multiply(lambda)));
  }
}
