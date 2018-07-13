// code by jph
package ch.ethz.idsc.owl.bot.tse2;

import ch.ethz.idsc.owl.bot.se2.Se2CarIntegrator;
import ch.ethz.idsc.owl.math.StateSpaceModel;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Cos;
import ch.ethz.idsc.tensor.sca.Sin;

/** Nonholonomic Wheeled Robot
 * 
 * @see Se2CarIntegrator */
public enum TSe2StateSpaceModel implements StateSpaceModel {
  INSTANCE;
  // ---
  @Override
  public Tensor f(Tensor x, Tensor u) {
    // x = {px, py, theta, vx}
    // u = {ax, ay == 0, rate, 0}
    // acceleration: positive for forward acceleration, negative for backward acceleration
    Scalar angle = x.Get(2);
    Scalar vx = x.Get(3);
    return Tensors.of( //
        Cos.FUNCTION.apply(angle).multiply(vx), // change in px
        Sin.FUNCTION.apply(angle).multiply(vx), // change in py
        u.Get(2).multiply(vx), // angular rate
        u.Get(0) // acceleration
    );
  }

  /** | f(x_1, u) - f(x_2, u) | <= L | x_1 - x_2 | */
  @Override
  public Scalar getLipschitz() {
    return RealScalar.ONE; // TODO check if correct
  }
}
