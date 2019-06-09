// code by jph
package ch.ethz.idsc.owl.bot.se2;

import ch.ethz.idsc.owl.bot.se2.glc.Se2CarFlows;
import ch.ethz.idsc.owl.bot.se2.twd.TwdFlows;
import ch.ethz.idsc.owl.math.StateSpaceModel;
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
 * 1) the tangent velocity [m*s^-1]
 * 2) the angular rate per second [s^-1]
 * 
 * for forward-only motion simply disallow negative velocity values
 * 
 * since the se2 state space model is parameter free,
 * the access to the model is via a singleton instance
 * 
 * | f(x_1, u) - f(x_2, u) | <= L | x_1 - x_2 |
 * Lipschitz L == 1
 *
 * @see Se2CarFlows
 * @see TwdFlows
 * @see Se2CarIntegrator */
public enum Se2StateSpaceModel implements StateSpaceModel {
  INSTANCE;
  // ---
  public static final int CONTROL_INDEX_VEL = 0;

  // ---
  @Override
  public Tensor f(Tensor x, Tensor u) {
    // return AngleVector.of(x.Get(2)).multiply(u.Get(0)).append(u.Get(2)); // <- short form
    // x = {px[m], py[m], theta[]}
    // u = {vx[m*s^-1], vy == 0, rate[s^-1]}
    // speed: positive for forward motion, or negative for backward motion
    Scalar angle = x.Get(2); // []
    Scalar vx = u.Get(0); // [m*s^-1]
    return Tensors.of( //
        Cos.FUNCTION.apply(angle).multiply(vx), // change in px
        Sin.FUNCTION.apply(angle).multiply(vx), // change in py
        u.Get(2) // angular rate per second [s^-1]
    );
  }
}
