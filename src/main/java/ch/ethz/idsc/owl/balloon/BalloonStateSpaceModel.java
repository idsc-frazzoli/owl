// code by astoll
package ch.ethz.idsc.owl.balloon;

import ch.ethz.idsc.owl.math.StateSpaceModel;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;

/** state space model taken from ? Chapter 2.7.2 by ?
 * 
 * @param x = {height [m], vertival velocity [m * s^-1], incremental air temperature (theta) [K]}
 * @param u = proportional of heat delivered to air mass by the burner [K * s^-1]
 * @author Andre */
/* package */ enum BalloonStateSpaceModel implements StateSpaceModel {
  INSTANCE;
  /* constants of the state space model */
  private static final Scalar TAU1 = Quantity.of(1, "s");
  private static final Scalar TAU2 = Quantity.of(2, "s");
  private static final Scalar SIGMA = Quantity.of(1, "m * K^-1 * s^-2");
  /* unknown perturbation due to vertical velocity of wind (assumed as -2 for now) */
  private Scalar w = Quantity.of(-2, "m * s^-1");

  @Override
  public Tensor f(Tensor x, Tensor u) {
    // y' = vel
    // vel' = (-1 / tau1) * vel + sigma * theta + w / tau2
    // theta' = - theta / tau1 + u
    // ---
    // Scalar y = x.Get(0);
    Scalar vel = x.Get(1);
    Scalar theta = x.Get(2);
    return Tensors.of(vel, //
        vel.negate().divide(TAU2).add(theta.multiply(SIGMA)).add(w.divide(TAU2)), //
        theta.negate().divide(TAU1).add(u.Get(0)));
  }

  @Override
  public Scalar getLipschitz() {
    throw new UnsupportedOperationException();
  }
}
