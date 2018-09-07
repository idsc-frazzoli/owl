// code by ynager, jph
package ch.ethz.idsc.owl.bot.tse2;

import ch.ethz.idsc.owl.bot.se2.Se2CarIntegrator;
import ch.ethz.idsc.owl.math.StateSpaceModel;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorScalarFunction;
import ch.ethz.idsc.tensor.sca.Cos;
import ch.ethz.idsc.tensor.sca.Sin;

/** Nonholonomic Wheeled Robot
 * 
 * @see Se2CarIntegrator */
public enum Tse2StateSpaceModel implements StateSpaceModel {
  INSTANCE;
  // ---
  public static final int STATE_INDEX_VEL = 3;
  public static final TensorScalarFunction STATE_VELOCITY = state -> state.Get(STATE_INDEX_VEL);
  public static final int CONTROL_INDEX_STEER = 0;
  public static final int CONTROL_INDEX_ACCEL = 1;

  // ---
  @Override
  public Tensor f(Tensor x, Tensor u) {
    // x = {px[m], py[m], theta[rad], vx[m*s^-1]}
    // u = {rate, ax}
    // acceleration: positive for forward acceleration, negative for backward acceleration
    Scalar angle = x.Get(2);
    Scalar vx = x.Get(STATE_INDEX_VEL);
    return Tensors.of( //
        Cos.FUNCTION.apply(angle).multiply(vx), // change in px
        Sin.FUNCTION.apply(angle).multiply(vx), // change in py
        u.Get(CONTROL_INDEX_STEER).multiply(vx), // angular rate
        u.Get(CONTROL_INDEX_ACCEL) // change in velocity
    );
  }

  /** | f(x_1, u) - f(x_2, u) | <= L | x_1 - x_2 | */
  @Override
  public Scalar getLipschitz() {
    throw new UnsupportedOperationException();
  }
}
