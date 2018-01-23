// code by jph
package ch.ethz.idsc.owl.bot.rice;

import java.io.Serializable;

import ch.ethz.idsc.owl.math.StateSpaceModel;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.red.Hypot;
import ch.ethz.idsc.tensor.sca.Exp;
import ch.ethz.idsc.tensor.sca.Sign;

/** Double Integrator with friction
 * 
 * Rice2StateSpaceModel is unit less.
 * When the use of units is required use {@link Duncan2StateSpaceModel}.
 * 
 * implementation for n-dimensional position and velocity */
public class Rice2StateSpaceModel implements StateSpaceModel, Serializable {
  public static StateSpaceModel of(Scalar mu) {
    return new Rice2StateSpaceModel(Exp.of(mu));
  }

  // ---
  private final Scalar lambda;

  /** @param lambda strictly positive friction coefficient */
  private Rice2StateSpaceModel(Scalar lambda) {
    this.lambda = Sign.requirePositive(lambda);
  }

  @Override // from StateSpaceModel
  public Tensor f(Tensor x, Tensor u) {
    Tensor v = x.extract(u.length(), x.length());
    return Join.of(v, u.subtract(v).multiply(lambda));
  }

  /** | f(x_1, u) - f(x_2, u) | <= L | x_1 - x_2 | */
  @Override
  public Scalar getLipschitz() {
    // theory tells that:
    // lipschitz const is 2-norm of 4x4 state space matrix
    // 0 0 1 0
    // 0 0 0 1
    // 0 0 L 0
    // 0 0 0 L
    // where L == -lambda
    // confirmed with mathematica
    return Hypot.BIFUNCTION.apply(RealScalar.ONE, lambda);
  }
}
