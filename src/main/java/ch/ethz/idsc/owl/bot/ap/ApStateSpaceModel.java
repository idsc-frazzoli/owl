// code by astoll
package ch.ethz.idsc.owl.bot.ap;

import ch.ethz.idsc.owl.math.StateSpaceModel;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Degree;
import ch.ethz.idsc.tensor.red.Times;
import ch.ethz.idsc.tensor.sca.Cos;
import ch.ethz.idsc.tensor.sca.Sin;

/** State-Space Model for Flying Aircraft
 * taken from "Validating a Hamilton-Jacobi Approximation to Hybrid System Reachable Sets⋆" by Ian Mitchel et al.
 * 
 * State space model was slightly altered as x and z are the first two entries of the vector
 * 
 * @author Andre Stoll
 * @param x = {horizontal distance (x), altitude (z), velocity, flight path angle (gamma)}
 * @param u = {thrust, angle of attack (aoa)} */
/* package */ enum ApStateSpaceModel implements StateSpaceModel {
  INSTANCE;
  // ---
  /** acceleration of gravity [m*s^-2] */
  private static final Scalar GRAVITY = RealScalar.of(9.81);
  /** total mass of airplane [kg] */
  private static final Scalar MASS = RealScalar.of(60_000);
  /** max thrust in N */
  public static final Scalar MAX_THRUST = RealScalar.of(160_000);
  /** max AOA in radian */
  public static final Scalar MAX_AOA = Degree.of(10);
  /** max speed [m*s^-1] */
  static final Scalar MAX_SPEED = RealScalar.of(83);
  /** stall speed [m*s^-1] */
  static final Scalar STALL_SPEED = RealScalar.of(58);
  /** steepest descent flight path angle */
  static final Scalar MAX_DESCENT_GAMMA = Degree.of(-15);
  /** maximal vertical touchdown speed in z direction in [m/s] */
  static final Scalar Z_0 = RealScalar.of(0.9144);
  /** altitude of final landing phase in [m] */
  static final Scalar ALTITUDE_FINAL_PHASE = RealScalar.of(20);

  @Override
  public Tensor f(Tensor x, Tensor u) {
    // x1' = x3*cos(x4)
    // x2' = x3*sin(x4)
    // x3' = 1/m * (u1*cos(u2) - D(u2,x3) - m*g*sin(x4))
    // x4' = 1/(m*x3) * (u1*sin(u2) + L(u2,x3) - m*g*cos(x4))
    // Scalar x1 = x.Get(0); // horizontal distance
    // Scalar x2 = x.Get(1); // altitude
    Scalar x3 = x.Get(2); // velocity
    Scalar x4 = x.Get(3); // flight path angle
    Scalar u1 = u.Get(0); // Thrust
    Scalar u2 = u.Get(1); // angle of attack
    return Tensors.of( //
        x3.multiply(Cos.of(x4)), //
        x3.multiply(Sin.of(x4)), //
        (u1.multiply(Cos.of(u2)).subtract(D(u2, x3)).subtract(Times.of(MASS, GRAVITY, Sin.of(x4)))).divide(MASS), //
        (u1.multiply(Sin.of(u2)).add(L(u2, x3)).subtract(Times.of(MASS, GRAVITY, Cos.of(x4)))).divide(MASS).divide(x3)//
    );
  }

  private static Scalar D(Scalar u2, Scalar x3) {
    // D(u2,x1) = (2.7 + 3.08 * (1.25 + 4.2 * u2^2) * x1^2
    double value = (1.25 + 4.2 * u2.number().doubleValue());
    return Times.of(RealScalar.of(2.7 + 3.08 * value * value), x3, x3);
  }

  private static Scalar L(Scalar u2, Scalar x3) {
    // L(u2,x1) = (68.6 * (1.25 + 4.2 * u2^2) * x1^2
    double value = (1.25 + 4.2 * u2.number().doubleValue());
    return Times.of(RealScalar.of(68.6 * value), x3, x3);
  }

  @Override
  public Scalar getLipschitz() {
    throw new UnsupportedOperationException();
  }
}
