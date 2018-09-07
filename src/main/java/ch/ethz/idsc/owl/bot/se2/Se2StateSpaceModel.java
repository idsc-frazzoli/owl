// code by jph
package ch.ethz.idsc.owl.bot.se2;

import ch.ethz.idsc.owl.bot.se2.glc.Se2CarFlows;
import ch.ethz.idsc.owl.bot.se2.twd.TwdFlows;
import ch.ethz.idsc.owl.math.StateSpaceModel;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Cos;
import ch.ethz.idsc.tensor.sca.Sin;

/** Nonholonomic Wheeled Robot
 * 
 * bapaden phd thesis: (5.5.12)
 * 
 * The Se2-StateSpaceModel has two control parameters:
 * 1) the angular rate
 * 2) the velocity
 * 
 * for forward-only motion simply disallow negative velocity values
 * 
 * since the se2 state space model is parameter free,
 * the access to the model is via a singleton instance
 *
 * @see Se2CarFlows
 * @see TwdFlows
 * @see Se2CarIntegrator */
public enum Se2StateSpaceModel implements StateSpaceModel {
  INSTANCE;
  // ---
  @Override
  public Tensor f(Tensor x, Tensor u) {
    // return AngleVector.of(x.Get(2)).multiply(u.Get(0)).append(u.Get(2)); // <- short form
    // x = {px, py, theta}
    // u = {vx, vy == 0, rate}
    // speed: positive for forward motion, or negative for backward motion
    Scalar angle = x.Get(2);
    Scalar vx = u.Get(0);
    return Tensors.of( //
        Cos.FUNCTION.apply(angle).multiply(vx), // change in px
        Sin.FUNCTION.apply(angle).multiply(vx), // change in py
        u.Get(2) // angular rate
    );
  }

  /** | f(x_1, u) - f(x_2, u) | <= L | x_1 - x_2 | */
  @Override
  public Scalar getLipschitz() {
    return RealScalar.ONE;
  }
}
