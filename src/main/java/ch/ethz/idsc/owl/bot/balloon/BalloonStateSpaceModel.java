// code by astoll
package ch.ethz.idsc.owl.bot.balloon;

import ch.ethz.idsc.owl.math.StateSpaceModel;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** state space model taken from the book
 * "Differentially Flat Systems" Chapter 2.7.2
 * by Hebertt Sira-Ramirez, Sunil K. Agrawal
 * 
 * @param x = {height [m], vertical velocity [m * s^-1], incremental air temperature (theta) [K]}
 * @param u = proportional of heat delivered to air mass by the burner [K * s^-1]
 * @author Andre */
/* package */ class BalloonStateSpaceModel implements StateSpaceModel {
  /** constants of the state space model */
  private final Scalar tau1;
  private final Scalar tau2;
  private final Scalar sigma;
  /** unknown perturbation due to vertical velocity of wind (assumed as -2 for now) */
  private final Scalar w;

  /** @param tau1
   * @param tau2
   * @param sigma
   * @param w */
  public BalloonStateSpaceModel(Scalar tau1, Scalar tau2, Scalar sigma, Scalar w) {
    this.tau1 = tau1;
    this.tau2 = tau2;
    this.sigma = sigma;
    this.w = w;
  }

  @Override
  public Tensor f(Tensor x, Tensor u) {
    // y' = vel
    // vel' = (-1 / tau1) * vel + sigma * theta + w / tau2
    // theta' = - theta / tau1 + u
    // ---
    // Scalar y = x.Get(0); // altitude
    Scalar vel = x.Get(1);
    Scalar theta = x.Get(2);
    return Tensors.of( //
        vel, //
        vel.negate().divide(tau2).add(theta.multiply(sigma)).add(w.divide(tau2)), //
        theta.negate().divide(tau1).add(u.Get(0)));
  }

  @Override
  public Scalar getLipschitz() {
    throw new UnsupportedOperationException();
  }
}
