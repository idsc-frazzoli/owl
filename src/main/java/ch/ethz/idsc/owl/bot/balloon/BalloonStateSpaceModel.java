// code by astoll
package ch.ethz.idsc.owl.bot.balloon;

import java.io.Serializable;

import ch.ethz.idsc.owl.math.model.StateSpaceModel;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Floor;

/** state space model taken from the book
 * "Differentially Flat Systems" Chapter 2.7.2
 * by Hebertt Sira-Ramirez, Sunil K. Agrawal
 * 
 * @param x = {position (horizontal) [m], height [m], vertical velocity [m*s^-1], incremental air temperature (theta) [K]}
 * @param u = proportional of heat delivered to air mass by the burner [K*s^-1] */
/* package */ class BalloonStateSpaceModel implements StateSpaceModel, Serializable {
  /** parameters of the state space model */
  private final Scalar tau1;
  private final Scalar tau2;
  private final Scalar sigma;
  private final boolean hasUnit;

  /** @param tau1 parameter with unit [s]
   * @param tau2 parameter with unit [s]
   * @param sigma parameter with unit [m*K^-1*s^-2]
   * @param hasUnit indicator is stateSpaceModel is used with units or not */
  public BalloonStateSpaceModel(Scalar tau1, Scalar tau2, Scalar sigma, boolean hasUnit) {
    this.tau1 = tau1;
    this.tau2 = tau2;
    this.sigma = sigma;
    this.hasUnit = hasUnit;
  }

  @Override // from StateSpaceModel
  public Tensor f(Tensor x, Tensor u) {
    /* x' = horizontal velocity
     * y' = vertical velocity
     * vel' = (-1 / tau2) * vel + sigma * theta + w / tau2
     * theta' = - theta / tau1 + u */
    @SuppressWarnings("unused")
    Scalar x1 = x.Get(0);
    Scalar y = x.Get(1); // altitude
    Scalar vel = x.Get(2);
    Scalar theta = x.Get(3);
    // =======
    /** unknown perturbation due to vertical velocity of wind */
    Scalar w = RealScalar.ONE.negate();// of( //
    // 2 * SimplexContinuousNoise.at(x1.number().doubleValue(), y.number().doubleValue(), vel.number().doubleValue(), theta.number().doubleValue()));
    /* unknown horizontal movement due to horizontal winds */
    Scalar x_dot = horizontalWinds(y);
    /* down force resulting from gravity and countered by air resistance, thus not 9.8 (approximate) */
    Scalar g = RealScalar.of(1);
    /* if stateSpaceModel is instantiated with units w and x_dot are given the necessary units,
     * [x_dot]= m*s^-1, [w] = m*s^-1 and [downForce] = m*s^-2 */
    if (hasUnit) {
      w = Quantity.of(w, "m*s^-1");
      x_dot = Quantity.of(x_dot, "m*s^-1");
      g = Quantity.of(g, "m*s^-2");
    }
    return Tensors.of( //
        x_dot, //
        vel, //
        vel.negate().divide(tau2).add(theta.multiply(sigma)).add(w.divide(tau2)).subtract(g), //
        theta.negate().divide(tau1).add(u.Get(0)));
  }

  // function not used
  static Scalar verticalWinds(Scalar y) {
    Scalar changeOfWindDirection = RealScalar.of(10);
    Clip altitude_clip = Clips.absolute(changeOfWindDirection);
    return altitude_clip.isInside(y) //
        ? RealScalar.of(+5)
        : RealScalar.of(-5);
  }

  public Scalar horizontalWinds(Scalar y) {
    if (hasUnit)
      y = ((Quantity) y).value();
    Scalar changeOfWindDirection = RealScalar.of(50);
    Scalar y_interval = y.divide(changeOfWindDirection);
    return Scalars.divides(RealScalar.of(2), Floor.FUNCTION.apply(y_interval)) //
        ? y.negate().multiply(RealScalar.of(0.05))
        : y.multiply(RealScalar.of(0.05));
  }
}
