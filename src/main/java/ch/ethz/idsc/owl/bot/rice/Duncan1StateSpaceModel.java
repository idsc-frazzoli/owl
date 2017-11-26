// code by jph
package ch.ethz.idsc.owl.bot.rice;

import ch.ethz.idsc.owl.math.StateSpaceModel;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.sca.Sign;

/** Single Integrator with friction
 * implementation for n-dimensional velocity
 * 
 * The state x (typically velocity) is bounded by "u_max / lambda"
 * where u_max is the maximum control (typically acceleration). */
public class Duncan1StateSpaceModel implements StateSpaceModel {
  private final Scalar lambda;

  /** @param lambda non-negative friction coefficient typically with unit [s^-1] */
  public Duncan1StateSpaceModel(Scalar lambda) {
    if (Sign.isNegative(lambda))
      throw TensorRuntimeException.of(lambda);
    this.lambda = lambda;
  }

  @Override // from StateSpaceModel
  public Tensor f(Tensor x, Tensor u) {
    Tensor v = x; // analogous to Duncan2StateSpaceModel
    return u.subtract(v.multiply(lambda));
  }

  /** | f(x_1, u) - f(x_2, u) | <= L | x_1 - x_2 | */
  @Override // from StateSpaceModel
  public Scalar getLipschitz() {
    return lambda;
  }
}
