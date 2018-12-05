// code by astoll
package ch.ethz.idsc.owl.bot.balloon;

import ch.ethz.idsc.owl.math.StateSpaceModel;
import ch.ethz.idsc.owl.math.noise.SimplexContinuousNoise;
import ch.ethz.idsc.tensor.RealScalar;
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

  /** @param tau1 parameter with unit [s]
   * @param tau2 parameter with unit [s]
   * @param sigma parameter with unit [m * K^-1 * s^-2] */
  public BalloonStateSpaceModel(Scalar tau1, Scalar tau2, Scalar sigma) {
    this.tau1 = tau1;
    this.tau2 = tau2;
    this.sigma = sigma;
  }

  @Override
  public Tensor f(Tensor x, Tensor u) {
    /* x' = ??
     * y' = vel
     * vel' = (-1 / tau2) * vel + sigma * theta + w / tau2
     * theta' = - theta / tau1 + u */
    Scalar x1 = x.Get(0);
    Scalar y = x.Get(1); // altitude
    Scalar vel = x.Get(2);
    Scalar theta = x.Get(3);
    /** unknown perturbation due to vertical velocity of wind
     * unknown horizontal movement due to horizontal winds
     * TODO change to something similar as in the DeltaDemo (imageGradientInterpolation) */
    double w = 2 * SimplexContinuousNoise.at(x1.number().doubleValue(), y.number().doubleValue(), vel.number().doubleValue(), theta.number().doubleValue());
    double x_dot = w * 3;
    // -----
    return Tensors.of( //
        RealScalar.of(x_dot), //
        vel, //
        vel.negate().divide(tau2).add(theta.multiply(sigma)).add(RealScalar.of(w).divide(tau2)), //
        theta.negate().divide(tau1).add(u.Get(0)));
  }

  @Override
  public Scalar getLipschitz() {
    throw new UnsupportedOperationException();
  }
}
