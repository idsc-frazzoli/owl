// code by astoll
package ch.ethz.idsc.owl.bot.balloon;

import ch.ethz.idsc.owl.math.StateSpaceModel;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;

/** state space model taken from the book
 * "Differentially Flat Systems" Chapter 2.7.2
 * by Hebertt Sira-Ramirez, Sunil K. Agrawal
 * 
 * @param x = {position [m], height [m], vertical velocity [m * s^-1], incremental air temperature (theta) [K]}
 * @param u = proportional of heat delivered to air mass by the burner [K * s^-1]
 * @author Andre */
/* package */ class BalloonStateSpaceModel implements StateSpaceModel {
  /** parameters of the state space model */
  private final Scalar tau1;
  private final Scalar tau2;
  private final Scalar sigma;
  private final boolean hasUnit;

  /** @param tau1 parameter with unit [s]
   * @param tau2 parameter with unit [s]
   * @param sigma parameter with unit [m * K^-1 * s^-2]
   * @param hasUnit indicator is stateSpaceModel is used with units or not */
  public BalloonStateSpaceModel(Scalar tau1, Scalar tau2, Scalar sigma, boolean hasUnit) {
    this.tau1 = tau1;
    this.tau2 = tau2;
    this.sigma = sigma;
    this.hasUnit = hasUnit;
  }

  @Override
  public Tensor f(Tensor x, Tensor u) {
    /* TODO ANDRE define x' properly
     * x' = ??
     * y' = vel
     * vel' = (-1 / tau2) * vel + sigma * theta + w / tau2
     * theta' = - theta / tau1 + u */
    Scalar x1 = x.Get(0);
    // System.out.println("x1 = " + x1);
    Scalar y = x.Get(1); // altitude
    // System.out.println("y = " + y);
    Scalar vel = x.Get(2);
    // System.out.println("vel = " + vel);
    Scalar theta = x.Get(3);
    System.out.println("theta = " + theta);
    // <<<<<<< HEAD
    System.out.println(u.Get(0));
    /* TODO change to something similar as in the DeltaDemo (imageGradientInterpolation) */
    // =======
    /* TODO ANDRE change to something similar as in the DeltaDemo (imageGradientInterpolation) */
    // >>>>>>> 2b08fc31f538326c38509d49a7c3e25ce86f853b
    /** unknown perturbation due to vertical velocity of wind */
    Scalar w = RealScalar.ONE.negate();// of( //
    // 2 * SimplexContinuousNoise.at(x1.number().doubleValue(), y.number().doubleValue(), vel.number().doubleValue(), theta.number().doubleValue()));
    /* unknown horizontal movement due to horizontal winds */
    // System.out.println("w = " + w);
    Scalar x_dot = RealScalar.ZERO;// verticalWinds(y);
    /* if stateSpaceModel is instantiated with units w and x_dot are given the necessary units,
     * [x]= m*s^-1 and [w] = m*s^-1 */
    if (hasUnit) {
      w = Quantity.of(w, "m*s^-1");
      x_dot = Quantity.of(x_dot, "m*s^1");
    }
    return Tensors.of( //
        x_dot, //
        vel, //
        vel.negate().divide(tau2).add(theta.multiply(sigma)).add(w.divide(tau2)), //
        theta.negate().divide(tau1).add(u.Get(0)));
  }

  public Scalar verticalWinds(Scalar y) {
    Scalar changeOfWindDirection = RealScalar.of(10);
    Clip altitude_clip = Clip.function(changeOfWindDirection.negate(), changeOfWindDirection);
    return altitude_clip.isInside(y) //
        ? RealScalar.of(5)
        : RealScalar.of(-5);
  }

  @Override
  public Scalar getLipschitz() {
    throw new UnsupportedOperationException();
  }
}
